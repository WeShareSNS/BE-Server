package com.weshare.api.v1.service.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weshare.api.v1.domain.schedule.Day;
import com.weshare.api.v1.domain.schedule.Expense;
import com.weshare.api.v1.repository.schedule.query.dto.SchedulePageFlatDto;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SchedulePageDto {
    private Long scheduleId;
    private long expense;
    private String username;
    private long likesCount;
    private long commentsCount;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate createdDate;

    @Builder(access = AccessLevel.PRIVATE)
    private SchedulePageDto(Long scheduleId, long expense, String username,
                            long likesCount, long commentsCount, LocalDateTime createDate) {
        this.scheduleId = scheduleId;
        this.expense = expense;
        this.username = username;
        this.likesCount = likesCount;
        this.commentsCount = commentsCount;
        this.createdDate = LocalDate.from(createDate);
    }

    public static SchedulePageDto from(SchedulePageFlatDto flatDto) {
        return SchedulePageDto.builder()
                .scheduleId(flatDto.getScheduleId())
                .expense(getTotalExpense(flatDto))
                .username(flatDto.getUser().getName())
                .likesCount(flatDto.getLikesCount())
                .commentsCount(flatDto.getCommentsCount())
                .createDate(flatDto.getCreatedDate())
                .build();
    }

    private static long getTotalExpense(SchedulePageFlatDto flatDto) {
        return flatDto.getDays().stream()
                .map(Day::getTotalDayExpense)
                .mapToLong(Expense::getExpense)
                .sum();
    }
}
