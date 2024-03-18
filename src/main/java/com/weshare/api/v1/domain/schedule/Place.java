package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.*;

import java.time.LocalTime;
import java.util.Objects;

@Embeddable
@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {

    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "time", nullable = false)
    private LocalTime time;
    @Column(name = "memo")
    private String memo;

    @Embedded
    @Column(name = "expense")
    private Expense expense;

    @Embedded
    @Column(name = "location", nullable = false)
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
        return Objects.equals(title, place.title) && Objects.equals(time, place.time) && Objects.equals(memo, place.memo) && Objects.equals(expense, place.expense) && Objects.equals(location, place.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, time, memo, expense, location);
    }
}
