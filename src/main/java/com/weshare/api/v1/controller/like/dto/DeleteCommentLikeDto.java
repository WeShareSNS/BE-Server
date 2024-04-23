package com.weshare.api.v1.controller.like.dto;

import com.weshare.api.v1.domain.user.User;

import java.util.Objects;

public record DeleteCommentLikeDto(
        Long commentId,
        Long likeId,
        User liker
) {
    public DeleteCommentLikeDto {
        Objects.requireNonNull(commentId);
        Objects.requireNonNull(likeId);
        Objects.requireNonNull(liker);
    }
}
