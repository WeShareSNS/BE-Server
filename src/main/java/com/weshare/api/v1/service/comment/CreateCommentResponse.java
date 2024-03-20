package com.weshare.api.v1.service.comment;

import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.Objects;

public record CreateCommentResponse(
        Long commentId,
        String username,
        String content,
        LocalDateTime createdDate
) {
    public CreateCommentResponse {
        Objects.requireNonNull(commentId);
        Objects.requireNonNull(createdDate);
        if (!StringUtils.hasText(username)) {
            throw new IllegalStateException("유저 정보가 올바르지 않습니다.");
        }
        if (!StringUtils.hasText(content)) {
            throw new IllegalStateException("댓글이 올바르지 않습니다.");
        }
    }
}
