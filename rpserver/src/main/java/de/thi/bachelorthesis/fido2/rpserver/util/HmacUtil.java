package de.thi.bachelorthesis.fido2.rpserver.util;

import javax.crypto.KeyGenerator;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class HmacUtil {
    public static final String HMAC_ALGORITHM = "HmacSHA256";

    public static SecretKey generateHmacKey() throws NoSuchAlgorithmException {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(HMAC_ALGORITHM);
        return keyGenerator.generateKey();
    }

    public static SecretKey getHmacKey(byte[] keyBytes) {
        return new SecretKeySpec(keyBytes, HMAC_ALGORITHM);
    }

    public static byte[] generateHmac(byte[] message, SecretKey secretKey) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(secretKey);
        return mac.doFinal(message);
    }

    public static byte[] generateHmac(byte[] message, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
        SecretKey secretKey = new SecretKeySpec(key, HMAC_ALGORITHM);
        Mac mac = Mac.getInstance(HMAC_ALGORITHM);
        mac.init(secretKey);
        return mac.doFinal(message);
    }
}
