package de.thi.bachelorthesis.fido2.rpserver.util;

import java.util.UUID;

public class AaguidUtil {
    public static String convert(byte[] aaguidBytes) {
        long msb = 0;
        long lsb = 0;
        for (int i = 0; i < 8; i++) {
            msb = (msb << 8) | (aaguidBytes[i] & 0xff);
        }
        for (int i = 8; i < 16; i++) {
            lsb = (lsb << 8) | (aaguidBytes[i] & 0xff);
        }
        return new UUID(msb, lsb).toString();
    }
}
