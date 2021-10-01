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

    /**
     * 将 int 数据转为 byte[] 数组，用于 TCP 数据传输。
     *
     * @param value 将要转为 byte 数组的值。
     * @return 转化后的数组。
     */
    public byte[] intToBytes(int value) {
        byte[] result = new byte[4];
        // 由高位到低位
        result[0] = (byte) ((value >> 24) & 0xFF);
        result[1] = (byte) ((value >> 16) & 0xFF);
        result[2] = (byte) ((value >> 8) & 0xFF);
        result[3] = (byte) (value & 0xFF);
        return result;
    }

    /**
     * 将 byte 数组转为 int 值。
     *
     * @param bytes 将要转换的 byte 数组。
     * @param start 转换的起始位置。
     * @param end 转换的结束位置。
     * @return 转换后的 int 值。
     */
    public int byteArrayToInt(byte[] bytes, int start, int end) {
        int value = 0;
        // 由高位到低位
        for (int i = start; i < end; i++) {
            int shift = (end - 1 - i) * 8;
            // 往高位游
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
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
