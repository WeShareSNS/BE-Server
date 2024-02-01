package com.weshare.api.v1.auth.controller;

public enum AuthErrorCode {

    BAD_REQUEST_ERROR(-4000),
    AUTH_LOGIN_BAD_REQUEST(-4001),
    PARAMETER_BAD_REQUEST_ERROR(-4002),
    TOKEN_TIME_OUT_ERROR(-4011),
    TOKEN_NOT_FOUND_ERROR(-4012),
    INVALID_TOKEN_ERROR(-4013),
    USER_NOT_FOUND_ERROR(-4040),
    EMAIL_DUPLICATE_ERROR(-4090),
    SERVER_ERROR(-5000);

    private int code;

    AuthErrorCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
