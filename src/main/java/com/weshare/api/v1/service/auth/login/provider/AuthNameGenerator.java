package com.weshare.api.v1.service.auth.login.provider;

import java.util.Random;

public final class AuthNameGenerator {
    private static final String EMAIL_REGEX = "@";
    private static final String UNIQUE_REGEX = "#";
    private static final int UNIQUE_NUMBER_LENGTH = 4;
    private AuthNameGenerator() {
    }

    public static String generateNameToEmail(String email) {
        String[] parts = email.split(EMAIL_REGEX);
        if (parts.length != 2) {
            throw new IllegalArgumentException("이메일 형식이 아닙니다.");
        }
        return parts[0];
    }

    public static String generateUniqueNameRandomized(String name) {
        final StringBuilder builder = new StringBuilder(getName(name));

        return builder.append(UNIQUE_REGEX)
                .append(Randoms.getRandomDigits(UNIQUE_NUMBER_LENGTH))
                .toString();
    }

    private static String getName(String name) {
        return name.split(UNIQUE_REGEX)[0];
    }

    static final class Randoms {
        private static final Random RANDOM = new Random();

        static String getRandomDigits(int length) {
            final StringBuilder builder = new StringBuilder();

            for (int i = 0; i < length; i++) {
                builder.append(RANDOM.nextInt(10));
            }
            return builder.toString();
        }
    }
}
