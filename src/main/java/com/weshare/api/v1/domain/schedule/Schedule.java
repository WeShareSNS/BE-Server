package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDate;
import java.util.List;

@Entity
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Destination destination;

    private LocalDate startDate;

    private LocalDate endDate;

    @Embedded
    private Days days;

    @Builder
    private Schedule(String title, Destination destination, LocalDate startDate, LocalDate endDate, Days days) {
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.days = days;
    }

    public long getTotalScheduleExpense() {
        return days.getTotalDaysExpense();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Destination getDestination() {
        return destination;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public List<Day> getDays() {
        return days.getDays();
    }
}
