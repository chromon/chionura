package com.chionura.codec;

import com.chionura.common.Global;

import java.util.HashMap;
import java.util.Map;

/**
 * 构建 Codec 编解码器。
 * 客户端和服务端可以通过 Codec 的 Type 创建 Codec 实例。
 */
public class CodecBuilder {

    /**
     * Codec 类型与实例 Map。
     * 其中 map 的 key 为 编解码器的类型，value 为实际的编解码器。
     */
    private static Map<Integer, Codec> codecMap;

    /**
     * 构造 Codec Map 并初始添加 JSON 编解码器.
     */
    public CodecBuilder() {
        codecMap = new HashMap<>();
        codecMap.put(Global.APPLICATIONJSON, new JSONCodec());
    }

    /**
     * 根据编解码器类型获取实际编解码器对象。
     *
     * @param type 解码器类型。
     * @return 实际编解码器对象。
     */
    public static Codec buildCodec(Integer type) {
        return codecMap.get(type);
    }

    /**
     * 添加额外的编解码器实现。
     *
     * @param type 解码器类型。
     * @param codec 实际编解码器对象。
     */
    public static void addCodec(Integer type, Codec codec) {
        codecMap.putIfAbsent(type, codec);
    }
}
