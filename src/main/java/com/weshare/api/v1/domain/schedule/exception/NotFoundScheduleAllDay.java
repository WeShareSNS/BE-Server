package com.weshare.api.v1.domain.schedule.exception;

public class NotFoundScheduleAllDay extends RuntimeException {
    public NotFoundScheduleAllDay() {
        super();
    }

    public NotFoundScheduleAllDay(String message) {
        super(message);
    }
}
