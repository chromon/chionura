package com.chionura.discovery;

import com.chionura.common.Constants;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 * 支持注册中心的服务发现。
 */
public class RegistryDiscovery extends MultiServersDiscovery {

    /**
     * 注册中心地址。
     */
    private String registryAddr;

    /**
     * 服务列表的过期时间。
     */
    private long timeout;

    /**
     * 代表最后从注册中心更新服务列表的时间，默认 10s 过期，
     * 即 10s 之后，需要从注册中心更新新的列表。
     */
    private long lastUpdate;

    /**
     * 日志。
     */
    private Logger log;

    /**
     * 根据注册中心地址和超时时间构建注册中心。
     *
     * @param registryAddr 注册中心地址，e.g. http://localhost:8001/_rpc_/registry
     * @param timeout 超时时间
     */
    public RegistryDiscovery(String registryAddr, long timeout) {
        this.registryAddr = registryAddr;

        if (timeout == 0) {
            this.timeout = Constants.DEFAULTUPDATETIMEPOUT;
        }
        log = Logger.getLogger(this.getClass().getName());
    }

    /**
     * 更新服务类别
     *
     * @return 错误信息。
     */
    @Override
    public synchronized String refresh() {
        long updateTime = this.lastUpdate + this.timeout;
        long currentTime = System.currentTimeMillis();

        if (updateTime > currentTime) {
            // 需要更新的时间还没到
            return null;
        }
        log.info("RPC registry: refresh servers from registry "
                + this.registryAddr);

        // 向服务注册中心发送 GET 请求，返回可用的服务列表。
        String resp = this.sendGetRequest(this.registryAddr);
        // [address=localhost:9911, address=localhost:9912]
        String[] strs = resp.substring(1, resp.length() - 1).split(", ");
        for (int i = 0; i < strs.length; i++) {
            strs[i] = strs[i].split("=")[1];
        }
        this.servers = strs;
        this.lastUpdate = System.currentTimeMillis();

        return null;
    }

    /**
     * 手动更新服务列表。
     *
     * @param servers 服务列表。
     * @return 错误信息。
     */
    @Override
    public synchronized String update(String[] servers) {
        this.servers = servers;
        this.lastUpdate = System.currentTimeMillis();
        return null;
    }

    /**
     * 根据负载均衡策略选择服务实例。
     *
     * @param selectMode 不同的负载均衡策略，随机、轮询、一致性哈希等
     * @return 服务实例。
     */
    @Override
    public String get(int selectMode) {
        // 需要先调用 Refresh 确保服务列表没有过期。
        String err = this.refresh();
        if (err != null) {
            log.severe("Registry Discover get error: " + err);
            return null;
        }
        return super.get(selectMode);
    }

    /**
     * 获取所有服务实例。
     *
     * @return 服务实例列表。
     */
    @Override
    public String[] getAll() {
        // 需要先调用 Refresh 确保服务列表没有过期。
        String err = this.refresh();
        if (err != null) {
            log.severe("Registry Discover getAll error: " + err);
            return null;
        }
        return super.getAll();
    }

    /**
     * 向远程服务器发送 GET 请求。
     *
     * @param url 请求的 URL.
     * @return 响应信息.
     */
    public String sendGetRequest(String url) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;

        try {
            URL realUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();

            // Set request properties.
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64)");

            // 建立连接。
            conn.connect();

            if (conn.getResponseCode() != 200) {
                System.err.println("server returned: " + conn.getResponseMessage());
                return null;
            }

            // 读取响应信息
            in = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return result.toString();
    }
}
