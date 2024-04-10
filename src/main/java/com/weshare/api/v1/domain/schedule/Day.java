package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.*;
import lombok.*;

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
    @CollectionTable(name = "places", joinColumns = @JoinColumn(name = "day_id"))
    private List<Place> places;
    @Column(name = "travel_date", nullable = false)
    private LocalDate travelDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "schedule_id")
    private Schedule schedule;
    @Builder
    private Day(Long id, List<Place> places, LocalDate travelDate) {
        this.id = id;
        this.places = places;
        this.travelDate = travelDate;
    }

    public void initSchedule(Schedule schedule) {
        this.schedule = schedule;
    }

    public Expense getTotalDayExpense() {
        return places.stream()
                .map(Place::getExpense)
                .reduce(Expense::sum)
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
