package com.weshare.api.v1.domain.schedule;

import com.weshare.api.v1.domain.BaseTimeEntity;
import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@ToString
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

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Embedded
    private Days days;

    @Builder
    private Schedule(String title, Destination destination, Days days) {
        this.title = title;
        this.destination = destination;
        this.days = days;
    }

    @Builder(builderMethodName = "conversionBuilder", buildMethodName = "conversionBuild")
    private Schedule(
            Long id, String title, Destination destination,
            Days dayDetails, User user, LocalDateTime createdDate) {
        this.id = id;
        this.title = title;
        this.destination = destination;
        this.days = dayDetails;
        this.user = user;
        setCreatedDate(createdDate);
    }

    public Schedule createSelfInstanceWithDays(Days dayDetails) {
        return conversionBuilder()
                .id(this.id)
                .title(this.title)
                .destination(this.destination)
                .dayDetails(dayDetails)
                .user(this.user)
                .createdDate(this.getCreatedDate())
                .conversionBuild();
    }

    public long getTotalScheduleExpense() {
        return days.getTotalDaysExpense();
    }

    public List<Day> getDays() {
        return days.getDays();
    }

    public LocalDate getStartDate() {
        return days.getStartDate();
    }

    public LocalDate getEndDate() {
        return days.getEndDate();
    }
}
