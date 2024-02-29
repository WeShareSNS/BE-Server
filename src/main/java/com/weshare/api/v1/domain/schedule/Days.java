package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Days {

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "schedule_id")
    private List<Day> days;

    public Days(List<Day> days) {
        this.days = days;
    }

    public boolean areAllDistinctDaysWithinRange(LocalDate startDate, LocalDate endDate) {

        final int size = days.stream()
                .filter(day -> day.isWithinDateRange(startDate, endDate))
                .map(Day::getTravelDate)
                .collect(Collectors.toSet())
                .size();

        return days.size() == size;
    }

    public boolean isDayCountMatching(LocalDate startDate, LocalDate endDate) {
        return endDate.compareTo(startDate) + 1 == days.size();
    }

    public Money getTotalDaysExpense() {
        return days.stream()
                .map(Day::getTotalDayExpense)
                .reduce(Money::sum)
                .orElseThrow(() -> new IllegalStateException("금액을 반환할 수 없습니다."));
    }

    public List<Day> getDays() {
        return Collections.unmodifiableList(days);
    }
}
