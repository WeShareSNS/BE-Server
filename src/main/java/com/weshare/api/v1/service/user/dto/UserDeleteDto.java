package com.weshare.api.v1.service.user.dto;

import java.time.LocalDateTime;
import java.util.Objects;

public record UserDeleteDto(
        Long userId,
        LocalDateTime deletedAt
) {
    public UserDeleteDto {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(deletedAt);
    }
}
