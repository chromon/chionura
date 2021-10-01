package com.chionura.codec;

import com.chionura.packet.Packet;

/**
 * Codec 对消息体进行编解码的接口。
 */
public interface Codec {

    /**
     * 对数据包编码。
     *
     * @param packet 待编码数据包。
     * @return 编码后的 byte 数组。
     */
    byte[] encodePacket(Packet packet);

    /**
     * 将传输过来的 byte 数组解码为 Packet 对象。
     *
     * @param byteArray 待解码 byte 数组。
     * @return 解码后的数据包。
     */
    Packet decodePacket(byte[] byteArray);
}
