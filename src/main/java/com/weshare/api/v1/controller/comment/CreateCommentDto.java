package com.weshare.api.v1.controller.comment;

import com.weshare.api.v1.domain.user.User;
import io.jsonwebtoken.lang.Assert;

import java.util.Objects;

public record CreateCommentDto(User user, Long scheduleId, String content) {
    public CreateCommentDto {
        Objects.requireNonNull(user);
        Objects.requireNonNull(scheduleId);
        Assert.hasText(content, "댓글을 입력해주세요");
    }
}
