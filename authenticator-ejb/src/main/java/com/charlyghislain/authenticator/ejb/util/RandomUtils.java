package com.charlyghislain.authenticator.ejb.util;

import com.charlyghislain.authenticator.domain.domain.util.CharacterSequences;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Random;

public class RandomUtils {


    public static final int RANDOM_PASSWORD_LENGTH = 48;

    public static String generatePasswordString() {
        Random random;
        try {
            random = SecureRandom.getInstance("SHA1PRNG");
        } catch (NoSuchAlgorithmException e) {
            random = new Random(System.currentTimeMillis());
        }
        return RandomUtils.generateString(random, CharacterSequences.ALPHANUMERIC, RANDOM_PASSWORD_LENGTH);
    }

    public static String generateString(@NonNull Random rng, @NonNull String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++) {
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        }
        return new String(text);
    }

}
