package com.weshare.api.v1.controller;

import lombok.Getter;

public enum ErrorCode {
    PARAMETER_BAD_REQUEST_ERROR(-4002, "잘못된 요청입니다."),
    TOKEN_TIME_OUT_ERROR(-4011, "토큰 시간이 만료되었습니다."),
    TOKEN_NOT_FOUND_ERROR(-4012, "토큰이 존재하지 않습니다."),
    INVALID_TOKEN_ERROR(-4013, "잘못된 토큰 입니다."),
    ACCESS_DENIED_ERROR(-4030, "잘못된 요청 입니다.");

    private static final String PREFIX = "[ERROR] ";
    @Getter
    private final int code;
    private final String message;

    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return PREFIX + message;
    }

    }
