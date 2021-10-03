package com.chionura.service;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * 服务端注册的服务。
 */
public class Service {

    /**
     * 服务名
     */
    private String serviceName;

    /**
     * 服务对应类对象
     */
    private Class<?> clazz;

    /**
     * 服务所包含方法
     */
    private Map<String, Method> methodMap;

    /**
     * 构造服务
     *
     * @param serviceName 服务名
     */
    public Service(String serviceName) {
        this.serviceName = serviceName;
        this.methodMap = new HashMap<>();
    }

    /**
     * 判断服务名是否可用。
     *
     * @return 是否是可用的服务
     */
    public boolean classIsAvailable() {
        boolean available = true;
        try {
            available = null != Class.forName(this.serviceName);
        } catch (Exception e) {
            available = false;
        }
        return available;
    }

    /**
     * 根据服务名构建服务。
     */
    public void newService() {
        try {
            this.clazz = Class.forName(serviceName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        this.getMethodsMap();
    }

    /**
     * 根据服务 Class 构建服务方法 Map。
     */
    private void getMethodsMap() {
        Method[] methods = this.clazz.getDeclaredMethods();
        for (Method method : methods) {
            methodMap.put(method.getName(), method);
        }
    }

    /**
     * 根据方法名查询服务方法对象。
     *
     * @param methodName 待查询方法名。
     * @return Method 实例对象，如果不存在返回 null。
     */
    public Method getMethod(String methodName) {
        return this.methodMap.get(methodName);
    }

    /**
     * 获取服务名。
     *
     * @return 服务名
     */
    public String getServiceName() {
        return serviceName;
    }
}
