package com.chionura.packet;

/**
 * 数据传输时的数据包，包括 header 和 body。
 */
public class Packet {

    /**
     * 数据包请求头。
     */
    private Header header;

    /**
     * 数据包实际数据。
     */
    private Object body;

    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        this.header = header;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }
}
