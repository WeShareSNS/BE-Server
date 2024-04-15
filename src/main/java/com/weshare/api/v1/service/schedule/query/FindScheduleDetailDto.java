package com.weshare.api.v1.service.schedule.query;

import lombok.Getter;

import java.util.Optional;

@Getter
public class FindScheduleDetailDto {
    private final Long scheduleId;
    private final Optional<Long> userId;

    public FindScheduleDetailDto(Long scheduleId, Optional<Long> userId) {
        this.scheduleId = scheduleId;
        this.userId = userId;
    }
}
