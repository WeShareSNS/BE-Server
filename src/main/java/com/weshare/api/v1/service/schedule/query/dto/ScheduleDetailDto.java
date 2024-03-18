package com.weshare.api.v1.service.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleDetailDto {
    private Long id;
    private String title;
    private Destination destination;
    private String username;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate createdDate;
    private List<DayDetailDto> dayDetail;

    @Builder(access = AccessLevel.PRIVATE)
    private ScheduleDetailDto(
            Long id, String title, Destination destination,
            String username, LocalDateTime createdDate , LocalDate startDate,
            LocalDate endDate, List<DayDetailDto> dayDetail
    ) {
        this.id = id;
        this.title = title;
        this.destination = destination;
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayDetail = dayDetail;
        this.createdDate = LocalDate.from(createdDate);
    }

    public static ScheduleDetailDto from(Schedule schedule) {
        return ScheduleDetailDto.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .destination(schedule.getDestination())
                .username(schedule.getUser().getName())
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

    public List<DayDetailDto> getDayDetail() {
        return Collections.unmodifiableList(dayDetail);
    }
}
