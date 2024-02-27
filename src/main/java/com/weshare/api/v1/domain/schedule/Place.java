package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;

@Embeddable
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Place {
    private LocalTime time;
    private String memo;
    private int expense;
    @Embedded
    private Location location;

    @Builder
    private Place(LocalTime time, String memo, int expense, Location location) {
        this.time = time;
        this.memo = memo;
        this.expense = expense;
        this.location = location;
    }
}
