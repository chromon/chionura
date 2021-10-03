package com.chionura.service;

import java.util.HashMap;
import java.util.Map;

/**
 * 服务注册类
 */
public class ServiceRegister {

    /**
     * 服务器中注册的服务 Map。
     */
    private static final Map<String, Service> SERVICE_MAP = new HashMap<>();

    /**
     * 将服务注册到服务器。
     *
     * @param service 待注册的服务。
     */
    public static void registerService(Service service) {
        SERVICE_MAP.putIfAbsent(service.getServiceName(), service);
    }

    /**
     * 根据服务名查找服务。
     *
     * @param serviceName 服务名
     * @return 服务，如果服务不存在则返回 null。
     */
    public static Service findService(String serviceName) {
        return SERVICE_MAP.get(serviceName);
    }
}
