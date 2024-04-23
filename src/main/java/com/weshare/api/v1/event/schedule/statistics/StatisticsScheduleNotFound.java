package com.weshare.api.v1.event.schedule.statistics;

public class StatisticsScheduleNotFound extends RuntimeException{
    public StatisticsScheduleNotFound() {
    }

    public StatisticsScheduleNotFound(String message) {
        super(message);
    }

    public StatisticsScheduleNotFound(String message, Throwable cause) {
        super(message, cause);
    }

    public StatisticsScheduleNotFound(Throwable cause) {
        super(cause);
    }
}
