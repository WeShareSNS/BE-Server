package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Day {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "day_id")
    private Long id;

    @ElementCollection
    @CollectionTable(
            name = "places",
            joinColumns = @JoinColumn(name = "day_id")
    )
    private List<Place> places;
    private LocalDate travelDate;

    @Builder
    private Day(List<Place> places, LocalDate travelDate) {
        this.places = places;
        this.travelDate = travelDate;
    }

    public Money getTotalDayExpense() {
        return places.stream()
                .map(Place::getExpense)
                .reduce(Money::sum)
                .orElseThrow(() -> new IllegalStateException("금액을 반환할 수 없습니다."));
    }

    public boolean isWithinDateRange(LocalDate startDate, LocalDate endDate) {
        boolean isAfterStart = !travelDate.isBefore(startDate);
        boolean isBeforeEnd = !travelDate.isAfter(endDate);

        return isBeforeEnd && isAfterStart;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Day day = (Day) object;
        return Objects.equals(id, day.id) && Objects.equals(places, day.places) && Objects.equals(travelDate, day.travelDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, places, travelDate);
    }
}
