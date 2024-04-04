package com.weshare.api.v1.controller.user;

import com.weshare.api.v1.service.schedule.query.dto.UserScheduleDto;

import java.util.List;

public record UserScheduleResponse(
        List<UserScheduleDto> content,
        int size
) {
}
