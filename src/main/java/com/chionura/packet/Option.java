package com.chionura.packet;

/**
 * RPC 协议附加信息，使用固定长度，用于指定随后的数据包信息，
 * 包括魔数、数据包长度和数据包编码类型。
 */
public class Option {

    /**
     * 魔数。
     */
    private int magicNum;

    /**
     * 数据包长度。
     */
    private int length;

    /**
     * 数据包编码类型默认为 1，即使用 JSON 编码。
     */
    private byte codecType = 1;

    /**
     * 空构造方法，用于客户端和服务端接收数据后构造 Option。
     */
    public Option() {}

    /**
     * 根据参数构造 Option，用于客户端和服务端发送数据。
     *
     * @param magicNum 魔数。
     * @param length 数据包长度。
     * @param codecType 数据包编码类型。
     */
    public Option(int magicNum, int length, byte codecType) {
        this.magicNum = magicNum;
        this.length = length;
        this.codecType = codecType;
    }

    public int getMagicNum() {
        return magicNum;
    }

    public void setMagicNum(int magicNum) {
        this.magicNum = magicNum;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public byte getCodecType() {
        return codecType;
    }

    public void setCodecType(byte codecType) {
        this.codecType = codecType;
    }
}
