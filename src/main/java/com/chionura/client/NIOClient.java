package com.chionura.client;

import com.chionura.codec.Codec;
import com.chionura.codec.CodecBuilder;
import com.chionura.common.Constants;
import com.chionura.packet.Header;
import com.chionura.packet.Option;
import com.chionura.packet.Packet;
import com.chionura.utils.DataUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.logging.Logger;

/**
 * RPC 请求客户端
 */
public class NIOClient {

    /**
     * Java NIO 轮询事件管理。
     */
    private Selector selector;

    /**
     * 打开 socket 通道
     */
    private SocketChannel socketChannel;

    /**
     * 服务端返回结果
     */
    private Object result;

    /**
     * 日志
     */
    private Logger log = Logger.getLogger(this.getClass().getName());

    /**
     * 通过端口号建立客户端连接。
     *
     * @param port 端口号。
     * @throws IOException IO Exception。
     */
    public NIOClient(int port) throws IOException {
        selector = Selector.open();

        // 打开 socket 通道
        socketChannel = SocketChannel.open();
        // 设置为非阻塞方式
        socketChannel.configureBlocking(false);
        // 注册连接服务端 socket 动作
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        // 服务器地址
        InetSocketAddress SERVER_ADDRESS = new InetSocketAddress(
                "localhost", port);
        // 建立连接
        socketChannel.connect(SERVER_ADDRESS);
    }

    /**
     * 客户端远程调用方法，用于发送请求。
     *
     * @param header 请求头
     * @throws IOException IO Exception
     */
    public Object call(Header header) throws IOException {
        // 判断一次 RPC 请求是否完成，包括发送和接收数据。
        boolean finished = true;

        // 轮询事件。
        while (finished) {
            // 选择一组键，其相应的通道已为 I/O 操作准备就绪。
            // 此方法执行处于阻塞模式的选择操作。
            selector.select();
            // 返回此选择器的已选择键集。
            Set<SelectionKey> selectionKeys = selector.selectedKeys();

            for (SelectionKey selectionKey : selectionKeys) {
                if (selectionKey.isConnectable()) {
                    // 接收连接。
                    accept(selectionKey);
                } else if (selectionKey.isReadable()) {
                    // 读取服务端返回数据。
                    read(selectionKey);
                    // 结束轮询。
                    finished = false;
                } else if (selectionKey.isWritable()) {
                    // 向服务端发送数据。
                    write(selectionKey, header);
                }
            }
            // 将已处理的事件移除。
            selectionKeys.clear();
        }
        return result;
    }

    /**
     * 从 ServerSocketChannel 中获取已建立好的连接 SocketChannel 并将其注册到 selector 中。
     *
     * @param selectionKey 连接事件。
     * @throws IOException IO Exception。
     */
    private void accept(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();
        // 判断此通道上是否正在进行连接操作。
        // 完成套接字通道的连接过程。
        if (socketChannel.isConnectionPending()) {
            socketChannel.finishConnect();
            log.info("完成连接!");
        }
        // 注册事件
        socketChannel.register(selector, SelectionKey.OP_WRITE);
    }

    /**
     * 读取服务端返回的数据。
     *
     * @param selectionKey 连接事件。
     * @throws IOException IO Exception.
     */
    private void read(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = null;
        try {
            // 返回创建此键的通道。
            socketChannel = (SocketChannel) selectionKey.channel();

            // 定义读取 Option 的 ByteBuffer 对象，且长度固定。
            ByteBuffer readBuffer = ByteBuffer.allocate(Constants.OPTIONLENGTH);

            // 读取服务器发送来的数据到缓冲区中。
            // 读取 Option，并记录读取长度。
            int optCount = socketChannel.read(readBuffer);

            // 当服务端关闭时，读取出错且读取的长度为 -1
            if (optCount == -1) {
                socketChannel.socket().close();
                socketChannel.close();
                selectionKey.cancel();
                log.warning("当前客户端连接已关闭");
            } else if (optCount > 0) {
                // 根据 ByteBuffer 中读取的数据构造 Option 实例。
                Option option = readOption(readBuffer.array());

                if (option.getMagicNum() != Constants.MAGICNUM) {
                    log.severe("服务端魔数错误，调用失败。");
                } else {
                    // 根据获取的数据包长度读取数据包
                    ByteBuffer buff = ByteBuffer.allocate(option.getLength());
                    int packetCount = socketChannel.read(buff);

                    if (packetCount == -1) {
                        socketChannel.socket().close();
                        socketChannel.close();
                        selectionKey.cancel();
                        log.warning("当前客户端连接已关闭");
                        return;
                    }

                    // 根据 Option 中的编码解码 packet 数据。
                    Codec codec = CodecBuilder.buildCodec(option.getCodecType());
                    Packet packet = codec.decodePacket(buff.array());

                    // 处理响应
                    handleResponse(packet);

                    System.out.println("body:" + packet.getBody());

                    System.out.println("服务器端接受客户端数据--:" + option.getMagicNum() + "-" + option.getLength() + "=" + new String(buff.array()));
                }

               socketChannel.register(selector, SelectionKey.OP_WRITE);
            }
        } catch (IOException e) {
            socketChannel.socket().close();
            socketChannel.close();
            selectionKey.cancel();
            log.warning("远程主机非正常关闭了一个现有的连接");
        }
    }

    /**
     * 根据 ByteBuffer 中读取的数据构造 Option 实例。
     *
     * @param bufferBytes ByteBuffer 转化的 byte 数组。
     * @return Option 实例。
     */
    private Option readOption(byte[] bufferBytes) {
        // 读取魔数，占前 4 个字节。
        int magicNum = DataUtils.byteArrayToInt(bufferBytes, 0, 4);
        // 读取数据包长度，占中间 4 个字节。
        int packetLen = DataUtils.byteArrayToInt(bufferBytes, 4, 8);
        // 读取编码类型，最后一个字节。
        byte codecType = bufferBytes[bufferBytes.length - 1];
        return new Option(magicNum, packetLen, codecType);
    }

    /**
     * 处理服务端响应。
     *
     * @param packet 数据包
     */
    private void handleResponse(Packet packet) {
        if (packet.getHeader().getError() != null) {
            log.severe(packet.getHeader().getError());
        } else {
            result = packet.getBody();
        }
    }

    /**
     * 向服务端发送数据。
     *
     * @param selectionKey 连接事件。
     * @throws IOException IO Exception.
     */
    private void write(SelectionKey selectionKey, Header header) throws IOException {
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        // 构造 Byte Buffer 并添加数据
        ByteBuffer writeBuffer = createWBuffer(header);
        // 将缓冲区各标志复位，并向 channel 中写入数据
        writeBuffer.flip();
        socketChannel.write(writeBuffer);

        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 构造写入数据的 Byte Buffer。
     *
     * @param header 数据包请求头
     * @return 创建的 ByteBuffer
     */
    private ByteBuffer createWBuffer(Header header) {
        // 构建请求 packet
        Packet packet = new Packet();
        packet.setHeader(header);

        // 将数据包进行编码
        Codec codec = CodecBuilder.buildCodec(Constants.APPLICATIONJSON);
        byte[] codecBytes = codec.encodePacket(packet);

        // 声明写入数据 buffer 并为之分配空间，包括 option 长度和编码后的 packet 长度。
        ByteBuffer buffer = ByteBuffer.allocate(Constants.OPTIONLENGTH + codecBytes.length);

        // 构造 Option
        Option opt = new Option(Constants.MAGICNUM, codecBytes.length, Constants.APPLICATIONJSON);

        // 写入数据
        buffer.put(opt.getMagicNumBytes());
        buffer.put(opt.getLengthBytes());
        buffer.put(opt.getCodecType());
        buffer.put(codecBytes);

        System.out.println("客户端向服务器端发送数据--：" + new String(codecBytes));

        return buffer;
    }

    /**
     * 关闭连接
     */
    public void close() {
        if (socketChannel.isConnected()) {
            try {
                socketChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
