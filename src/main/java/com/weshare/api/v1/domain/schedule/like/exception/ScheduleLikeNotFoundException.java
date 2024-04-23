package com.weshare.api.v1.domain.schedule.like.exception;

public class ScheduleLikeNotFoundException extends RuntimeException {
    public ScheduleLikeNotFoundException() {
        super();
    }

    public ScheduleLikeNotFoundException(String message) {
        super(message);
    }
}
