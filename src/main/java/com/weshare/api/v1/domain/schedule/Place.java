package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

import java.time.LocalTime;
import java.util.Objects;

@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    private LocalTime time;
    private String memo;

    @Embedded
    private Money expense;

    @Embedded
    private Location location;

    @Builder
    private Place(LocalTime time, String memo, String expense, Location location) {
        this.time = time;
        this.memo = memo;
        this.expense = new Money(expense);
        this.location = location;
    }

    public Money getExpense() {
        return expense;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Place place = (Place) object;
        return Objects.equals(time, place.time) && Objects.equals(memo, place.memo) && Objects.equals(expense, place.expense) && Objects.equals(location, place.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(time, memo, expense, location);
    }
}
