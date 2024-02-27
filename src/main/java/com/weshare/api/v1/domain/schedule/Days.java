package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Days {

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true)
    @JoinColumn(name = "schedule_id")
    private List<Day> days;

    public Days(List<Day> days) {
        this.days = days;
    }

    public long getTotalDaysExpense() {
        return days.stream()
                .mapToLong(Day::getTotalDayExpense)
                .sum();
    }

    public List<Day> getDays() {
        return Collections.unmodifiableList(days);
    }
}
