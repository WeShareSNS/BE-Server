package com.weshare.api.v1.event.user;

import java.time.LocalDateTime;
import java.util.Objects;

public record UserDeletedEvent(
        Long userId,
        LocalDateTime deletedAt
) {
    public UserDeletedEvent {
        Objects.requireNonNull(userId);
        Objects.requireNonNull(deletedAt);
    }
}
