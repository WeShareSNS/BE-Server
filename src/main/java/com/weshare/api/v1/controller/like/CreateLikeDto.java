package com.weshare.api.v1.controller.like;

import com.weshare.api.v1.domain.user.User;

import java.util.Objects;

public record CreateLikeDto(
        Long scheduleId,
        User liker
) {
    public CreateLikeDto {
        Objects.requireNonNull(scheduleId);
        Objects.requireNonNull(liker);
    }
}
