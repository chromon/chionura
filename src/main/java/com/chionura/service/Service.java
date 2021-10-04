package com.chionura.service;

import java.lang.reflect.Method;
import java.util.List;
import java.util.logging.Logger;

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
     * 构造服务
     *
     * @param serviceName 服务名
     */
    public Service(String serviceName) {
        this.serviceName = serviceName;
    }

    /**
     * 判断服务名是否可用。
     *
     * @return 是否是可用的服务
     */
    public boolean classIsAvailable() {
        boolean available;
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
    }

    /**
     * 根据方法名查询服务方法对象。
     *
     * @param methodName 待查询方法名。
     * @param argsType 参数类型。
     * @return Method 实例对象，如果不存在返回 null。
     */
    private Method getMethod(String methodName, Class<?>... argsType) {
        Method method = null;
        try {
            method = this.clazz.getDeclaredMethod(methodName, argsType);
        } catch (NoSuchMethodException e) {
            Logger log = Logger.getLogger("Service");
            log.severe("java.lang.NoSuchMethodException:\n" + e.getMessage());
        }
        return method;
    }

    /**
     * 根据提供的方法名和参数列表判断方法是否可用。
     *
     * @param methodName 方法名
     * @param args 参数列表
     * @return 方法是否可用。
     */
    public boolean isMethodAvailable(String methodName, Object... args) {
        Class<?>[] argsType = this.getArgsType(args);
        Method method = this.getMethod(methodName, argsType);
        return method != null;
    }

    /**
     * 根据方法名和参数列表调用方法并返回。
     *
     * @param methodName 方法名
     * @param args 参数列表
     * @return 方法返回值
     */
    public Object call(String methodName, Object... args) {
        Object result = null;
        try {
            Class<?>[] argsType = this.getArgsType(args);

            Object obj = this.clazz.getDeclaredConstructor().newInstance();
            Method method = this.getMethod(methodName, argsType);
            result = method.invoke(obj, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据参数获取参数类型。
     *
     * @param args 可变参数对象。
     * @return 参数类型数组。
     */
    private Class<?>[] getArgsType(Object... args) {
        List<Object> argsList = List.of(args);

        // 完整实现
        // List<Class<?>> classList = argsList.stream()
        //         .map(arg -> arg.getClass()).collect(Collectors.toList());
        // Class<?>[] objects = classList.toArray(new Class<?>[classList.size()]);

        return argsList.stream()
                .map(Object::getClass).toArray(Class<?>[]::new);
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
