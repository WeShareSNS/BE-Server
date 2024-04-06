package com.weshare.api.v1.event.schedule;

import java.util.Objects;

public record ScheduleCreatedEvent(
        Long scheduleId,
        long totalExpense
) {
    public ScheduleCreatedEvent {
        Objects.requireNonNull(scheduleId);
    }
}
