package com.chionura.common;

/**
 * 定义全局通用数据。
 */
public class Constants {

    /**
     * Option 长度。
     */
    public static final int OPTIONLENGTH = 9;

    /**
     * Magic Num.
     */
    public static final int MAGICNUM = 0xacebabe;

    /**
     * JSON 编码格式。
     */
    public static final Byte APPLICATIONJSON = 1;

    /**
     * 服务端处理请求的超时时间，单位为秒。
     */
    public static final int TIMEOUT = 1;

    /**
     * 负载均衡策略：随机
     */
    public static final int RANDOMSELECT = 1;

    /**
     * 负载均衡策略：轮询
     */
    public static final int ROUNDROBINSELECT = 2;

    /**
     * 负载均衡策略：一致性哈希
     */
    public static final int CONSISTENTHASHSELECT = 3;
}
