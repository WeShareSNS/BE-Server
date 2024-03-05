package com.weshare.api.v1.controller.auth;

import lombok.Getter;

public enum AuthErrorCode {

    BAD_REQUEST_ERROR(-4000),
    AUTH_LOGIN_BAD_REQUEST(-4001),
    PARAMETER_BAD_REQUEST_ERROR(-4002),
    USER_NOT_FOUND_ERROR(-4040),
    EMAIL_DUPLICATE_ERROR(-4090);

    @Getter
    private final int code;

    AuthErrorCode(int code) {
        this.code = code;
    }
}
