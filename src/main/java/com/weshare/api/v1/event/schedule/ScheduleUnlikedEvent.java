package com.weshare.api.v1.event.schedule;

import java.util.Objects;

public record ScheduleUnlikedEvent(
    Long scheduleId
) {
    public ScheduleUnlikedEvent {
        Objects.requireNonNull(scheduleId);
    }
}
