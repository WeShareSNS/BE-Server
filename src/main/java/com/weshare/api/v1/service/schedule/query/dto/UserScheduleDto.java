package com.weshare.api.v1.service.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record UserScheduleDto(
        Long scheduleId,
        String title,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm a", timezone = "Asia/Seoul", locale = "en_US")
        LocalDateTime createAt
) {
}
