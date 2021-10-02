package com.chionura.utils;

/**
 * 数据转换相关工具。
 */
public class DataUtils {

    /**
     * 将 int 数据转为 byte[] 数组，用于 TCP 数据传输。
     *
     * @param value 将要转为 byte 数组的值。
     * @return 转化后的数组。
     */
    public static byte[] intToBytes(int value) {
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
    public static int byteArrayToInt(byte[] bytes, int start, int end) {
        int value = 0;
        // 由高位到低位
        for (int i = start; i < end; i++) {
            int shift = (end - 1 - i) * 8;
            // 往高位游
            value += (bytes[i] & 0x000000FF) << shift;
        }
        return value;
    }
}
