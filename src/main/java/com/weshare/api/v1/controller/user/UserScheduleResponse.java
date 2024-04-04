package com.weshare.api.v1.controller.user;

import com.weshare.api.v1.service.schedule.query.dto.UserScheduleDto;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "내가 작성한 모든 여행일정 조회 응답 API")
public record UserScheduleResponse(
        @Schema(title = "사용자가 작성한 여행일정", description = "사용자가 작성한 여행일정")
        List<UserScheduleDto> content,
        @Schema(title = "사용자가 작성한 여행일정 개수", description = "사용자가 작성한 여행일정의 총 개수입니다.")
        int size
) {
}
