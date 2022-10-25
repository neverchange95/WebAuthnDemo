package de.thi.bachelorthesis.fido2.rpserver.util;

import java.security.SecureRandom;
import java.util.Base64;

public class ChallengeGenerator {
    public static String generate(int byteSize) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] challengeBytes = new byte[byteSize];
        secureRandom.nextBytes(challengeBytes);

        //base 64 url encoding
        return Base64.getUrlEncoder().withoutPadding().encodeToString(challengeBytes);
    }
}
