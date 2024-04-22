package com.weshare.api.v1.domain.schedule;

import com.weshare.api.v1.domain.BaseTimeEntity;
import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Schedule extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private Long id;

    private String title;

    @Enumerated(EnumType.STRING)
    private Destination destination;

    @Column(columnDefinition = "integer default 0", nullable = false)
    private int viewCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private Days days;

    @Builder
    public Schedule(
            Long id,
            String title,
            Destination destination,
            int viewCount,
            User user,
            Days days
    ) {
        this.id = id;
        this.title = title;
        this.destination = destination;
        this.viewCount = viewCount;
        this.user = user;
        this.days = days;
    }

    @Builder(builderMethodName = "conversionBuilder", buildMethodName = "conversionBuild")
    private Schedule(
            Long id,
            String title,
            Destination destination,
            int viewCount,
            Days dayDetails,
            User user,
            LocalDateTime createdDate
    ) {
        this.id = id;
        this.title = title;
        this.destination = destination;
        this.days = dayDetails;
        this.user = user;
        this.viewCount = viewCount;
        setCreatedDate(createdDate);
    }

    public Schedule createSelfInstanceWithDays(Days dayDetails) {
        return conversionBuilder()
                .id(this.id)
                .title(this.title)
                .destination(this.destination)
                .dayDetails(dayDetails)
                .viewCount(this.viewCount)
                .user(this.user)
                .createdDate(this.getCreatedDate())
                .conversionBuild();
    }

    public void initDays() {
        days.initDays(this);
    }

    public boolean isContainDays(List<Day> updateDays) {
        return days.isContainDays(updateDays);
    }

    public void updateDestinationOrTitle(Destination destination, String title) {
        this.title = title;
        if (!destination.isEmpty()) {
            this.destination = destination;
        }
    }

    public List<Day> getDays() {
        return days.getDays();
    }

    public long getTotalScheduleExpense() {
        return days.getTotalDaysExpense();
    }

    public LocalDate getStartDate() {
        return days.getStartDate();
    }

    public LocalDate getEndDate() {
        return days.getEndDate();
    }

    public void incrementViewCount() {
        this.viewCount += 1;
    }

    public boolean isSameScheduleId(Long scheduleId) {
        return this.id.equals(scheduleId);
    }
}
