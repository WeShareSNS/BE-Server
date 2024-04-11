package com.weshare.api.v1.service.schedule.command;

import io.jsonwebtoken.lang.Assert;

public record DeleteScheduleDto(
        Long userId,
        Long scheduleId
) {
    public DeleteScheduleDto {
        Assert.notNull(scheduleId, "잘못된 요청입니다.");
    }
}
