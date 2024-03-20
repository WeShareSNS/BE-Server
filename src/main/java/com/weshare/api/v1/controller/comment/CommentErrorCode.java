package com.weshare.api.v1.controller.comment;

import lombok.Getter;

public enum CommentErrorCode {

    BAD_REQUEST_ERROR(-4000,"요청이 올바르지 않습니다."),
    PARAMETER_BAD_REQUEST_ERROR(-4002, "파라미터를 확인해주세요"),
    SCHEDULE_NOT_FOUND_ERROR(-4041, "해당하는 여행일정이 존재하지 않습니다.");

    private static final String PREFIX = "[ERROR] ";
    @Getter
    private final int code;
    private final String message;

    CommentErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return PREFIX + message;
    }
}
