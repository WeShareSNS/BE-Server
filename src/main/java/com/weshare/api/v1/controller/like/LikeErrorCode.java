package com.weshare.api.v1.controller.like;

import lombok.Getter;

public enum LikeErrorCode {

    BAD_REQUEST_ERROR(-4000,"요청이 올바르지 않습니다."),
    PARAMETER_BAD_REQUEST_ERROR(-4002, "파라미터를 확인해주세요"),
    SCHEDULE_NOT_FOUND_ERROR(-4041, "해당하는 여행일정이 존재하지 않습니다."),
    COMMENT_NOT_FOUND_ERROR(-4042, "해당하는 댓글이 존재하지 않습니다."),
    SCHEDULE_LIKE_NOT_FOUND_ERROR(-4043, "여행 일정 좋아요가 존재하지 않습니다."),
    COMMENT_LIKE_NOT_FOUND_ERROR(-4044, "댓글 좋아요가 존재하지 않습니다."),
    DUPLICATE_LIKE_ERROR(-4093, "이미 해당하는 좋아요를 등록한 사용자 입니다.");

    private static final String PREFIX = "[ERROR] ";
    @Getter
    private final int code;
    private final String message;

    LikeErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getMessage() {
        return PREFIX + message;
    }
}
