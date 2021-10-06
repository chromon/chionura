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
     * 负载均衡策略：随机。
     */
    public static final int RANDOMSELECT = 1;

    /**
     * 负载均衡策略：轮询。
     */
    public static final int ROUNDROBINSELECT = 2;

    /**
     * 负载均衡策略：一致性哈希。
     */
    public static final int CONSISTENTHASHSELECT = 3;

    /**
     * 默认服务注册中心地址。
     */
    public static final String DEFAULTPATH = "/_rpc_/registry";

    /**
     * 服务默认超时时间 5 min。任何注册的服务超过 5 min，即视为不可用状态。
     */
    public static final long DEFAULTTIMEOUT = 1000 * 60 * 5;

    /**
     * 代表最后从注册中心更新服务列表的时间，默认 10s 过期，
     * 即 10s 之后，需要从注册中心更新新的列表。
     */
    public static final long DEFAULTUPDATETIMEPOUT = 1000 * 10;
}
