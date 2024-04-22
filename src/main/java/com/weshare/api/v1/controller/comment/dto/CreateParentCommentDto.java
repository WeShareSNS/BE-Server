package com.weshare.api.v1.controller.comment.dto;

import com.weshare.api.v1.domain.user.User;
import io.jsonwebtoken.lang.Assert;

import java.util.Objects;

public record CreateParentCommentDto(
        User commenter,
        Long scheduleId,
        String content
) {
    public CreateParentCommentDto {
        Objects.requireNonNull(commenter);
        Objects.requireNonNull(scheduleId);
        Assert.hasText(content, "댓글을 입력해주세요");
    }
}
