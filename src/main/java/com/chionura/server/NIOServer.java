package com.chionura.server;

import com.chionura.codec.Codec;
import com.chionura.codec.CodecBuilder;
import com.chionura.common.Constants;
import com.chionura.packet.Header;
import com.chionura.packet.Option;
import com.chionura.packet.Packet;
import com.chionura.service.Service;
import com.chionura.service.ServiceRegister;
import com.chionura.utils.DataUtils;
import com.chionura.utils.TimeoutUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

/**
 * RPC 请求服务端
 */
public class NIOServer {

    /**
     * 多路 IO 复用器 Selector
     */
    private Selector selector;

    /**
     * 请求数据包
     */
    private Packet packet;

    /**
     * 日志
     */
    private Logger log;

    /**
     * 构造服务端通道，等待连接事件。
     *
     * @param port 服务端绑定端口。
     * @throws IOException IO 异常
     */
    public NIOServer(String host, int port) throws IOException {
        // 打开服务器套接字通道
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        // 服务器配置为非阻塞
        serverSocketChannel.configureBlocking(false);
        // 检索与此通道关联的服务器套接字
        ServerSocket serverSocket = serverSocketChannel.socket();
        // 进行服务的绑定
        serverSocket.bind(new InetSocketAddress(host, port));

        // 通过 open 方法找到 Selector
        selector = Selector.open();
        // 注册到 selector，等待连接，服务器通道只能注册 SelectionKey.OP_ACCEPT 事件
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        log = Logger.getLogger(this.getClass().getName());
        log.info("Server start at port: " + port);
    }

    /**
     * 监听服务端事件
     */
    @SuppressWarnings("InfiniteLoopStatement")
    public void listen() throws IOException {
        while (true) {
            // 选择一组键，并且相应的通道已经打开
            selector.select();
            // 返回选择器已选择键集。
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();
            while (iterator.hasNext()) {
                SelectionKey selectionKey = iterator.next();
                // 移除处理过的事件，方式重复处理
                iterator.remove();
                handleKey(selectionKey);
            }
        }
    }

    /**
     * 处理事件请求。
     *
     * @param selectionKey 选择器监听事件。
     * @throws IOException IO 异常
     */
    private void handleKey(SelectionKey selectionKey) throws IOException {
        // 测试此键的通道是否已准备好接受新的 TCP 套接字连接。
        if (selectionKey.isAcceptable()) {
            // 接收客户端连接
            accept(selectionKey);
        } else if (selectionKey.isReadable()) {
            // 读取客户端数据
            read(selectionKey);
        } else if (selectionKey.isWritable()) {
            // 向客户端写入数据
            write(selectionKey);
        }
    }

    /**
     * 在 ServerSocketChannel 接收和准备好一个新的 TCP 连接后，
     * 返回一个新的 SocketChannel，但是这个新的 SocketChannel 并没有在 Selector
     * 选择器中注册，所以程序还没法通过 Selector 通知这个 SocketChannel 的事件。
     * 所以得到新的 SocketChannel 后，需要到 selector 中注册这个 SocketChannel 感兴趣的事件。
     *
     * @param selectionKey Selector 监听 Channel 的事件。
     * @throws IOException IO Exception.
     */
    private void accept(SelectionKey selectionKey) throws IOException {
        // 返回为之创建此键的通道。
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) selectionKey.channel();
        // 接受到此通道套接字的连接。
        // 此方法返回的套接字通道（如果有）将处于阻塞模式。
        SocketChannel socketChannel = serverSocketChannel.accept();
        // 配置为非阻塞
        socketChannel.configureBlocking(false);
        // 注册到 Selector，等待连接
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * read 方法用于读取从客户端传来的信息。
     *
     * @param selectionKey Selector 监听 Channel 的事件。
     * @throws IOException IO Exception.
     */
    private void read(SelectionKey selectionKey) throws IOException {
        SocketChannel socketChannel = null;
        try {
            // 返回为此键创建的通道。
            socketChannel = (SocketChannel) selectionKey.channel();
            ByteBuffer readBuffer = ByteBuffer.allocate(Constants.OPTIONLENGTH);

            // 读取服务器发送来的数据到缓冲区中
            // 读取 option
            int optionCount = socketChannel.read(readBuffer);
            if (optionCount == -1) {
                socketChannel.socket().close();
                socketChannel.close();
                selectionKey.cancel();
                log.warning("当前客户端连接已关闭");
            } else if (optionCount > 0) {
                // 根据 ByteBuffer 中读取的数据构造 Option 实例。
                Option opt = readOption(readBuffer.array());

                if (opt.getMagicNum() != Constants.MAGICNUM) {
                    // 魔数不一致
                    log.severe("客户端请求出错，魔数不一致。");
                    socketChannel.close();
                } else {
                    // 读取 packet
                    ByteBuffer buff = ByteBuffer.allocate(opt.getLength());
                    int packetCount = socketChannel.read(buff);

                    if (packetCount == -1) {
                        socketChannel.socket().close();
                        socketChannel.close();
                        selectionKey.cancel();
                        log.warning("当前客户端连接已关闭");
                    }

                    // 根据 Option 中的编码解码 packet 数据。
                    Codec codec = CodecBuilder.buildCodec(opt.getCodecType());
                    packet = codec.decodePacket(buff.array());

                    System.out.println("服务器端接受客户端数据--:"+ opt.getMagicNum() + "-" + opt.getLength() + "=" + new String(buff.array()));

                    // 处理调用请求
                    String error = handleReqTimeout();
                    if (error != null) {
                        packet.getHeader().setError(error);
                    }

                    socketChannel.register(selector, SelectionKey.OP_WRITE);
                }
            }
        } catch (IOException e) {
            socketChannel.socket().close();
            socketChannel.close();
            selectionKey.cancel();
            log.info("远程主机非正常关闭了一个现有的连接");
        }
    }

    /**
     * 根据 ByteBuffer 中读取的数据构造 Option 实例。
     *
     * @param bufferBytes ByteBuffer 转化的 byte 数组。
     * @return Option 实例。
     */
    private Option readOption(byte[] bufferBytes) {
        // magic number，占前 4 个字节
        int magicNum = DataUtils.byteArrayToInt(bufferBytes, 0, 4);
        // packet length，占中间 4 个字节
        int packetLen = DataUtils.byteArrayToInt(bufferBytes, 4, 8);
        // codec type，占最后一个字节
        byte codecType = bufferBytes[bufferBytes.length - 1];

        return new Option(magicNum, packetLen, codecType);
    }

    /**
     * 处理服务方法调用请求。
     *
     * @return 在处理超时情况下返回错误信息。
     */
    private String handleRequest() {
        Header header = packet.getHeader();
        // 获取服务名和方法名
        String serviceMethod = header.getServiceMethod();
        String serviceName = serviceMethod.substring(0,
                serviceMethod.lastIndexOf("."));
        String methodName = serviceMethod.substring(
                serviceMethod.lastIndexOf(".") + 1);

        // 参数列表
        Object[] args = header.getArgs();
        Service service = ServiceRegister.findService(serviceName);

        if (service == null) {
            // 服务无效
            packet.getHeader().setError("ERROR: RPC 服务 '" + serviceName + "' 不是有效的服务！");
        } else {
            if (!service.isMethodAvailable(methodName, args)) {
                // 方法无效
                packet.getHeader().setError("ERROR: RPC 服务方法 '" + methodName + "' 无效！");
            } else {
                if (args.length == 0) {
                    // 无参方法
                    packet.setBody(service.call(methodName));
                } else {
                    // 有参方法
                    packet.setBody(service.call(methodName, args));
                }
            }
        }
        return null;
    }

    /**
     * 服务调用请求添加超时处理。
     *
     * @return 超时处理错误信息。
     */
    public String handleReqTimeout() {
        return TimeoutUtils.process(this::handleRequest, Constants.TIMEOUT);
    }

    /**
     * Write 方法用于向客户端写入信息。
     *
     * @param selectionKey Selector 监听 Channel 的事件。
     * @throws IOException IO Exception.
     */
    private void write(SelectionKey selectionKey) throws IOException {
        // 返回为之创建此键的通道。
        SocketChannel socketChannel = (SocketChannel) selectionKey.channel();

        // 写入数据缓冲区
        ByteBuffer writeBuffer = createWBuffer();

        // 将缓冲区各标志复位
        writeBuffer.flip();
        //输出到通道
        socketChannel.write(writeBuffer);
        socketChannel.register(selector, SelectionKey.OP_READ);
    }

    /**
     * 构造写入数据的 Byte Buffer。
     *
     * @return 创建的 ByteBuffer
     */
    private ByteBuffer createWBuffer() {
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

        log.info("服务端向客户端发送数据--：" + new String(codecBytes));

        return buffer;
    }
}
