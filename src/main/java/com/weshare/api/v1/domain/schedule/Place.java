package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

import java.time.LocalTime;

@Embeddable
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    private LocalTime time;
    private String memo;

    @Embedded
    private Money expense;

    @Embedded
    private Location location;

    @Builder
    private Place(LocalTime time, String memo, long expense, Location location) {
        this.time = time;
        this.memo = memo;
        this.expense = new Money(expense);
        this.location = location;
    }

    public long getExpense() {
        return expense.getValue();
    }
}
