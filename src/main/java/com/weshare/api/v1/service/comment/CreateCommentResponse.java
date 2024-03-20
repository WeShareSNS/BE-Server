package com.weshare.api.v1.service.comment;

import org.springframework.util.StringUtils;

import java.util.Objects;

public record CreateCommentResponse(
        Long scheduleId,
        String username,
        String content
) {
    public CreateCommentResponse {
        Objects.requireNonNull(scheduleId);
        if (!StringUtils.hasText(username)) {
            throw new IllegalStateException("유저 정보가 올바르지 않습니다.");
        }
        if (!StringUtils.hasText(content)) {
            throw new IllegalStateException("댓글이 올바르지 않습니다.");
        }
    }
}
