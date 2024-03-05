package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

import java.time.LocalTime;
import java.util.Objects;

@Embeddable
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    private String title;
    private LocalTime time;
    private String memo;

    @Embedded
    private Expense expense;

    @Embedded
    private Location location;

    @Builder
    private Place(String title, LocalTime time, String memo, Expense expense, Location location) {
        this.title = title;
        this.time = time;
        this.memo = memo;
        this.expense = expense;
        this.location = location;
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
