package com.chionura.discovery;

import com.chionura.common.Constants;

import java.util.Arrays;
import java.util.Random;

/**
 * 不需要注册中心，服务列表由手工维护的服务发现实例。
 */
public class MultiServersDiscovery implements Discovery {

    /**
     * 随机数实例，用于生成随机访问服务实例。
     */
    private Random random;

    /**
     * 服务实例列表。
     */
    public String[] servers;

    /**
     * 记录轮询算法（Round Robin）轮询到的位置，
     * 初始化时随机值，避免每次从 0 开始。
     */
    private int index;

    public MultiServersDiscovery() {
        this.servers = new String[0];
        this.random = new Random();
        this.index = random.nextInt(Integer.MAX_VALUE - 1);
    }

    /**
     * 构造实例，并使用随机值初始化 index。
     *
     * @param servers 服务实例列表。
     */
    public MultiServersDiscovery(String[] servers) {
        this.servers = servers;
        this.random = new Random();
        this.index = random.nextInt(Integer.MAX_VALUE - 1);
    }

    /**
     * 由于该服务发现实例是手动维护，所以 refresh 方法没意义，可以忽略。
     *
     * @return 不作操作，返回 null。
     */
    @Override
    public String refresh() {
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
        return null;
    }

    /**
     * 根据负载均衡策略选择服务实例。
     *
     * @param selectMode 不同的负载均衡策略，随机、轮询、一致性哈希等
     * @return 服务实例。
     */
    @Override
    public synchronized String get(int selectMode) {

        int len = this.servers.length;
        System.out.println(len);
        System.out.println(Arrays.toString(this.servers));

        if (len == 0) {
            return "RPC 服务发现：没有可用的服务实例！";
        }

        switch (selectMode) {
            case Constants.RANDOMSELECT:
                return this.servers[this.random.nextInt(len)];
            case Constants.ROUNDROBINSELECT:
                String server = this.servers[this.index % len];
                this.index = (this.index + 1) % len;
                return server;
            case Constants.CONSISTENTHASHSELECT:
                // TODO
                return null;
            default:
                return "RPC 服务发现：没有支持的负载均衡策略";
        }
    }

    /**
     * 获取所有服务实例。
     *
     * @return 服务实例列表。
     */
    @Override
    public String[] getAll() {
        return Arrays.copyOf(this.servers, this.servers.length);
    }
}
