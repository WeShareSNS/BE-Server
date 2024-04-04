package com.weshare.api.v1.service.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "내가 작성한 여행일정 개별 응답")
public record UserScheduleDto(
        @Schema(title = "사용자가 작성한 여행일정 id", description = "사용자가 작성한 여행일정 id")
        Long scheduleId,
        @Schema(title = "사용자가 작성한 여행일정 제목", description = "사용자가 작성한 여행일정 제목")
        String title,
        @Schema(title = "사용자가 작성한 여행일정 등록 날짜", description = "사용자가 작성한 여행일정 등록 날짜")
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm a", timezone = "Asia/Seoul", locale = "en_US")
        LocalDateTime createAt
) {
}
