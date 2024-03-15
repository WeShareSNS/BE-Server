package com.weshare.api.v1.controller.schedule.query.advice;


import lombok.Getter;

public enum ScheduleQueryErrorCode {

    BAD_REQUEST_ERROR(-4000, "입력이 올바르지 않습니다."),
    SCHEDULE_NOT_FOUND_ERROR(-4041, "해당하는 글이 존재하지 않습니다."),;

    private static final String PREFIX = "[ERROR] ";

    @Getter
    private final int code;
    private final String message;

    ScheduleQueryErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return PREFIX + message;
    }

}
