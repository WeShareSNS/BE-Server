package com.weshare.api.v1.controller.like.dto;

import com.weshare.api.v1.domain.user.User;

import java.util.Objects;

public record CreateScheduleLikeDto(
        Long scheduleId,
        User liker
) {
    public CreateScheduleLikeDto {
        Objects.requireNonNull(scheduleId);
        Objects.requireNonNull(liker);
    }
}
