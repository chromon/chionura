package com.chionura.registry;

/**
 * 服务相关项。
 */
public class ServerItem {

    /**
     * 服务地址。
     */
    private String address;

    /**
     * 服务起始时间。
     */
    private long startTime;

    /**
     * 构造服务项。
     *
     * @param address 服务地址。
     * @param startTime 服务起始时间。
     */
    public ServerItem(String address, long startTime) {
        this.address = address;
        this.startTime = startTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }
}
