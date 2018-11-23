package com.charlyghislain.authenticator.example.app.validation;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public class CharacterValidationUtils {

    public static boolean allMatch(@Nullable CharSequence value, @NonNull CharSequence characterListToMatch) {
        if (value == null) {
            return false;
        }
        return value.chars()
                .mapToObj(i -> (char) i)
                .allMatch(c -> CharacterValidationUtils.anyMatch(characterListToMatch, c));
    }


    public static boolean anyMatch(@Nullable CharSequence value, @NonNull CharSequence characterListToMatch) {
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
