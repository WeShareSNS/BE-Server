package com.weshare.api.v1.service.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weshare.api.v1.domain.schedule.Schedule;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleDetailDto {
    private Long scheduleId;
    private String title;
    private String destination;
    private String userName;
    @Setter
    private boolean isLiked;
    private int viewCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate createdDate;
    private List<DayDetailDto> dayDetail;

    @Builder(access = AccessLevel.PRIVATE)
    private ScheduleDetailDto(
            Long scheduleId,
            String title,
            String destination,
            String username,
            LocalDateTime createdDate,
            LocalDate startDate,
            LocalDate endDate,
            List<DayDetailDto> dayDetail,
            int viewCount
    ) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.destination = destination;
        this.userName = username;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayDetail = dayDetail;
        this.viewCount = viewCount;
        this.createdDate = LocalDate.from(createdDate);
    }

    public static ScheduleDetailDto from(Schedule schedule) {
        return ScheduleDetailDto.builder()
                .scheduleId(schedule.getId())
                .title(schedule.getTitle())
                .destination(schedule.getDestination().getName())
                .username(schedule.getUser().getName())
                .viewCount(schedule.getViewCount())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .dayDetail(createDayDetails(schedule))
                .createdDate(schedule.getCreatedDate())
                .build();
    }

    private static List<DayDetailDto> createDayDetails(Schedule schedule) {
        return schedule.getDays().stream()
                .map(DayDetailDto::from)
                .toList();
    }
}
