package de.thi.bachelorthesis.fido2.rpserver.util;

import java.nio.ByteBuffer;
import java.util.Arrays;

public class UnsignedUtil {
    public static int readUINT16BE(byte[] input) {
        return ((input[0] & 0xFF) << 8) |
                (input[1] & 0xFF);
    }

    public static long readUINT32BE(byte[] input) {
        return ((input[0] & 0xFFL) << 24) |
                ((input[1] & 0xFFL) << 16) |
                ((input[2] & 0xFFL) << 8) |
                (input[3] & 0xFFL);
    }

    public static byte[] writeUINT32BE(long input) {
        byte[] bytes = new byte[8];
        ByteBuffer.wrap(bytes).putLong(input);

        return Arrays.copyOfRange(bytes, 4, 8);
    }
}
