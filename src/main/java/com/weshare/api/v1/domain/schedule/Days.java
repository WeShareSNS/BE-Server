package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Days {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "schedule_id")
    private List<Day> days;
    @Column(name = "start_date",nullable = false)
    private LocalDate startDate;
    @Column(name = "end_date",nullable = false)
    private LocalDate endDate;

    public Days(List<Day> days, LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("시작, 종료 날짜가 올바르지 않습니다.");
        }
        if (!areAllDistinctDaysWithinRange(days,startDate, endDate) || !isDayCountMatching(days, startDate, endDate)) {
            throw new IllegalArgumentException("날짜 정보가 올바르지 않습니다.");
        }
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days;
    }

    private boolean areAllDistinctDaysWithinRange(List<Day> days, LocalDate startDate, LocalDate endDate) {
        final int size = days.stream()
                .filter(day -> day.isWithinDateRange(startDate, endDate))
                .map(Day::getTravelDate)
                .collect(Collectors.toSet())
                .size();

        return days.size() == size;
    }

    private boolean isDayCountMatching(List<Day> days, LocalDate startDate, LocalDate endDate) {
        return endDate.compareTo(startDate) + 1 == days.size();
    }

    public long getTotalDaysExpense() {
        return days.stream()
                .map(Day::getTotalDayExpense)
                .reduce(Expense::sum)
                .orElseThrow(() -> new IllegalStateException("금액을 반환할 수 없습니다."))
                .getExpense();
    }

    public List<Day> getDays() {
        return Collections.unmodifiableList(days);
    }
}
