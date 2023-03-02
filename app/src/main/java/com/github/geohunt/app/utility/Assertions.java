package com.github.geohunt.app.utility;

public class Assertions {
    private Assertions() {

    }

    public static <T> void assertNonNull(T value) {
        if (value == null) {
            throw new AssertionError("The value should not be NULL");
        }
    }
}
