package com.charlyghislain.authenticator.domain.domain.util;

public class CharacterValidationUtils {

    public static boolean allMatch(CharSequence value, CharSequence characterListToMatch) {
        if (value == null) {
            return false;
        }
        return value.chars()
                .mapToObj(i -> (char) i)
                .allMatch(c -> CharacterValidationUtils.anyMatch(characterListToMatch, c));
    }


    public static boolean anyMatch(CharSequence value, CharSequence characterListToMatch) {
        if (value == null) {
            return false;
        }
        return value.chars()
                .mapToObj(i -> (char) i)
                .anyMatch(c -> CharacterValidationUtils.anyMatch(characterListToMatch, c));
    }

    private static boolean anyMatch(CharSequence value, Character characterToMatch) {
        return value.chars()
                .mapToObj(i -> (char) i)
                .anyMatch(characterToMatch::equals);
    }
}
