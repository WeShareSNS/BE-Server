package com.weshare.api.v1.controller.like.dto;

import com.weshare.api.v1.domain.user.User;

import java.util.Objects;

public record CreateCommentLikeDto(
        Long commentId,
        User liker
) {
    public CreateCommentLikeDto {
        Objects.requireNonNull(commentId);
        Objects.requireNonNull(liker);
    }
}
