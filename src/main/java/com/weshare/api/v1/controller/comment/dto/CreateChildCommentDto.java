package com.weshare.api.v1.controller.comment.dto;

import com.weshare.api.v1.domain.user.User;
import io.jsonwebtoken.lang.Assert;

import java.util.Objects;

public record CreateChildCommentDto(
        User commenter,
        Long scheduleId,
        Long parentCommentId,
        String content
) {
    public CreateChildCommentDto {
        Objects.requireNonNull(commenter);
        Objects.requireNonNull(scheduleId);
        Objects.requireNonNull(parentCommentId);
        Assert.hasText(content, "댓글을 입력해주세요");
    }
}
