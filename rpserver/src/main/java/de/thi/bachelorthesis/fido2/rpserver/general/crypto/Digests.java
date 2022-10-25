package de.thi.bachelorthesis.fido2.rpserver.general.crypto;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Digests {

    private Digests() {
    }

    public static byte[] sha256(byte[] data) {
        return digest("SHA-256", data);
    }

    public static byte[] sha1(byte[] data) {
        return digest("SHA1", data);
    }

    public static byte[] digest(String alg, byte[] data) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(alg);

            return messageDigest.digest(data);
        } catch(NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
