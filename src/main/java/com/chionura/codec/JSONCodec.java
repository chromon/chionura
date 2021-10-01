package com.chionura.codec;

import com.chionura.packet.Packet;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

/**
 * 使用 JSON 格式对数据包编解码。
 */
public class JSONCodec implements Codec {

    /**
     * 使用 jackson 对数据包进行 JSON 格式编解码。
     */
    private ObjectMapper objectMapper;

    public JSONCodec() {
        objectMapper = new ObjectMapper();
    }

    /**
     * 使用 JSON 对数据包编码。
     *
     * @param packet 待编码数据包。
     * @return 编码后的 byte 数组。
     */
    @Override
    public byte[] encodePacket(Packet packet) {
        String s = null;
        try {
            s = objectMapper.writeValueAsString(packet);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return s != null? s.getBytes(): null;
    }

    /**
     * 使用 JSON 将传输过来的 byte 数组解码为 Packet 对象。
     *
     * @param byteArray 待解码 byte 数组。
     * @return 解码后的数据包。
     */
    @Override
    public Packet decodePacket(byte[] byteArray) {
        Packet packet = null;
        try {
            packet = objectMapper.readValue(byteArray, Packet.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return packet;
    }
}
