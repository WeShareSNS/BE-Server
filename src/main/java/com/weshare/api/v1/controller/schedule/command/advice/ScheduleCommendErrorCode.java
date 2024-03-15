package com.weshare.api.v1.controller.schedule.command.advice;


import lombok.Getter;

public enum ScheduleCommendErrorCode {

    BAD_REQUEST_ERROR(-4000, "입력이 올바르지 않습니다."),
    USER_NOT_FOUND_ERROR(-4040, "사용자를 찾을 수 없습니다.");

    private static final String PREFIX = "[ERROR] ";

    @Getter
    private final int code;
    private final String message;

    ScheduleCommendErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return PREFIX + message;
    }

}
