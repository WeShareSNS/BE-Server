package com.weshare.api.v1.event.schedule;

import java.util.Objects;

public record ScheduleLikedEvent(
    Long scheduleId
) {
    public ScheduleLikedEvent {
        Objects.requireNonNull(scheduleId);
    }
}
