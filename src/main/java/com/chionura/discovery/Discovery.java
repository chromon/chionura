package com.chionura.discovery;

/**
 * 服务发现。
 * 用于负载均衡时在多个服务实例间选择。
 */
public interface Discovery {

    /**
     * 从注册中心更新服务列表。
     *
     * @return 错误信息。
     */
    String refresh();

    /**
     * 手动更新服务列表。
     *
     * @param servers 服务列表。
     * @return 错误信息。
     */
    String update(String[] servers);

    /**
     * 根据负载均衡策略选择服务实例。
     *
     * @param selectMode 不同的负载均衡策略，随机、轮询、一致性哈希等
     * @return 服务实例。
     */
    String get(int selectMode);

    /**
     * 获取所有服务实例。
     *
     * @return 服务实例列表。
     */
    String[] getAll();
}
