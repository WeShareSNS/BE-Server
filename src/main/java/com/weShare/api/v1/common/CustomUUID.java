package com.weShare.api.v1.common;

import java.util.UUID;

public final class CustomUUID {

    private static final String UUID_DEFAULT_REGEX = "-";
    private static final int DEFAULT_START_NUMBER = 0;
    private CustomUUID() {
    }

    public static String getCustomUUID(int length, String regex) {
        return getRandomUUID()
                .replaceAll(UUID_DEFAULT_REGEX, regex)
                .substring(DEFAULT_START_NUMBER, length);
    }

    private static String getRandomUUID() {
        return UUID.randomUUID().toString();
    }
}
