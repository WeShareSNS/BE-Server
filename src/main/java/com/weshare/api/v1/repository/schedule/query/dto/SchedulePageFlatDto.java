package com.weshare.api.v1.repository.schedule.query.dto;

import com.weshare.api.v1.domain.schedule.Destination;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchedulePageFlatDto {
    private Long scheduleId;
    private String title;
    private Destination destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String username;
    private LocalDateTime createdDate;
    private Boolean isLiked;

    public SchedulePageFlatDto(
            Long scheduleId,
            String title,
            Destination destination,
            LocalDate startDate,
            LocalDate endDate,
            String username,
            LocalDateTime createdDate,
            boolean isLiked
    ) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.username = username;
        this.createdDate = createdDate;
        this.isLiked = isLiked;
    }

}
