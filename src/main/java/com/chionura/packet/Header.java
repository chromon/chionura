package com.chionura.packet;

import java.util.UUID;

/**
 * RPC 数据包请求头。
 */
public class Header {

    /**
     * 客户端发送的请求
     * 包括服务名和方法名
     */
    private String serviceMethod;

    /**
     * 请求的序号，也可以认为是某个请求的 ID，用来区分不同的请求。
     */
    private String sequence;

    /**
     * 请求参数数组。
     */
    private Object[] args;

    /**
     * 错误信息，客户端置为空，服务端如果如果发生错误，将错误信息置于 error 中。
     */
    private String error;

    /**
     * 空参构造方法，用于初始化序列号。
     */
    public Header() {
        this.sequence = UUID.randomUUID().toString();
    }

    public String getServiceMethod() {
        return serviceMethod;
    }

    public void setServiceMethod(String serviceMethod) {
        this.serviceMethod = serviceMethod;
    }

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object ...args) {
        this.args = args;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
