package com.weshare.api.v1.controller.like.dto;

import com.weshare.api.v1.domain.user.User;

import java.util.Objects;

public record DeleteScheduleLikeDto(
        Long scheduleId,
        Long likeId,
        User liker
) {
    public DeleteScheduleLikeDto {
        Objects.requireNonNull(scheduleId);
        Objects.requireNonNull(likeId);
        Objects.requireNonNull(liker);
    }
}
