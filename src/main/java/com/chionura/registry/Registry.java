package com.chionura.registry;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.*;
import java.util.*;

/**
 * 服务注册中心。
 */
public class Registry implements HttpHandler {

    /**
     * 服务默认超时时间，默认为 5 min。
     * 也就是说，任何注册的服务超过 5 min，即视为不可用状态。
     * 超时时间为 0 则永不超时。
     */
    private long timeout;

    /**
     * 服务集合。
     */
    private Map<String, ServerItem> serversMap;

    /**
     * 通过超时时间构造服务注册实例。
     *
     * @param timeout 超时时间。
     */
    public Registry(long timeout) {
        this.timeout = timeout;
        this.serversMap = new HashMap<>();
    }

    /**
     * 项注册中心添加服务实例。
     *
     * @param address 待添加的服务实例地址。
     */
    public synchronized void putServer(String address) {
        ServerItem server = this.serversMap.get(address);
        if (server == null) {
            this.serversMap.put(address,
                    new ServerItem(address, System.currentTimeMillis()));
        } else {
            // 如果服务存在，则更新起始时间为当前时间。
            server.setStartTime(System.currentTimeMillis());
        }
    }

    /**
     * 返回可用的服务列表，如果存在超时的服务，则删除。
     *
     * @return 服务列表。
     */
    public synchronized String[] aliveServers() {
        List<String> alive = new ArrayList<>();

        Iterator<Map.Entry<String, ServerItem>> it = this.serversMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, ServerItem> entry = it.next();
            long endTime = entry.getValue().getStartTime() + this.timeout;
            if (this.timeout == 0 || (endTime > System.currentTimeMillis())) {
                // 超时时间为 0 即永不超时，或当前时间尚未超时（结束时间比当前时间大）。
                alive.add(entry.getKey());
            } else {
                // 超时则从服务集合中删除。
                it.remove();
            }
        }
        Collections.sort(alive);
        return alive.toArray(new String[0]);
    }

    /**
     * 处理 HTTP 请求。
     *
     * @param exchange HttpExchange.
     * @throws IOException IO Exception.
     */
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String result = "";

        OutputStream os = exchange.getResponseBody();

        switch(method) {
            case "GET":
                // 处理 GET 请求，返回服务列表。
                exchange.sendResponseHeaders(200, 0);
                // 返回可用的服务列表。
                result = Arrays.toString(this.aliveServers());
                os.write(result.getBytes());
                os.close();
            case "POST":
                // 处理 POST 请求，向服务列表中添加服务。
                StringBuilder sb = new StringBuilder();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        exchange.getRequestBody()));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }

                if (sb.toString().equals("")) {
                    exchange.sendResponseHeaders(500, 0);
                }
                this.putServer(sb.toString());

                exchange.sendResponseHeaders(200, 0);
                result = "Post server '" + sb.toString() + "' success!";
                os.write(result.getBytes());
                os.close();
        }
    }
}
