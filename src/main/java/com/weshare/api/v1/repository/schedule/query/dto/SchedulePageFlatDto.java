package com.weshare.api.v1.repository.schedule.query.dto;

import com.weshare.api.v1.domain.schedule.Day;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.user.User;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchedulePageFlatDto {
    private Long scheduleId;
    private String title;
    private Destination destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private User user;
    private LocalDateTime createdDate;
    private long likesCount;
    private long commentsCount;
    @Setter
    private List<Day> days;

    public SchedulePageFlatDto(
            Long scheduleId, String title, Destination destination,
            LocalDate startDate, LocalDate endDate, User user,
            LocalDateTime createdDate, long likesCount, long commentsCount
    ) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.user = user;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.createdDate = createdDate;
    }

    public List<Day> getDays() {
        return Collections.unmodifiableList(days);
    }
}
