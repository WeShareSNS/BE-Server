package com.weshare.api.v1.controller.schedule.query;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weshare.api.v1.domain.schedule.Destination;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SearchScheduleDto {
    private Long scheduleId;
    private String title;
    private Destination destination;
    private long expense;
    private String userName;
    private int likesCount;
    private int commentsCount;
    private int viewCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate createdDate;
    private boolean isLiked;

    @Builder
    private SearchScheduleDto(
            Long scheduleId,
            String title,
            Destination destination,
            long expense,
            String userName,
            int likesCount,
            int commentsCount,
            int viewCount,
            LocalDate startDate,
            LocalDate endDate,
            LocalDate createdDate,
            boolean isLiked) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.destination = destination;
        this.expense = expense;
        this.userName = userName;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.viewCount = viewCount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.createdDate = createdDate;
        this.isLiked = isLiked;
    }
}
