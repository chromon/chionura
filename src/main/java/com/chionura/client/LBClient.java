package com.chionura.client;

import com.chionura.discovery.Discovery;
import com.chionura.packet.Header;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 支持负载均衡的客户端。
 */
public class LBClient {

    /**
     * 服务器发现实例。
     */
    private Discovery discovery;

    /**
     * 负载均衡模式。
     */
    private int selectMode;

    /**
     * 已创建成功的客户端实例，e.g. key："127.0.0.1:9911"
     */
    private Map<String, NIOClient> clientsMap;

    /**
     * 构造客户端。
     *
     * @param discovery 服务发现实例
     * @param selectMode 负载均衡模式
     */
    public LBClient(Discovery discovery, int selectMode) {
        this.discovery = discovery;
        this.selectMode = selectMode;
        this.clientsMap = new HashMap<>();
    }

    /**
     * 获取客户端，如果不存在则创建。
     *
     * @param addr 连接地址，e.g. "127.0.0.1:9911"
     * @return 客户端实例。
     */
    private synchronized NIOClient dial(String addr) throws IOException {
        NIOClient nioClient = this.clientsMap.get(addr);
        if (nioClient != null && !nioClient.isAvailable()) {
            nioClient.close();
            this.clientsMap.remove(addr);
            nioClient = null;
        }

        if (nioClient == null) {
            String[] addrs = addr.split(":");
            nioClient = new NIOClient(addrs[0], Integer.parseInt(addrs[1]));
            this.clientsMap.putIfAbsent(addr, nioClient);
        }
        return nioClient;
    }

    /**
     * 支持负载均衡的服务方法调用。
     *
     * @param header 请求头。
     * @return 方法调用结果。
     * @throws IOException IO 异常。
     */
    public Object call(Header header) throws IOException {
        String rpcAddr = this.discovery.get(this.selectMode);
        NIOClient client = this.dial(rpcAddr);
        return client.call(header);
    }
}
