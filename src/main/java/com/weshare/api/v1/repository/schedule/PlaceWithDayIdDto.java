package com.weshare.api.v1.repository.schedule;

import com.weshare.api.v1.domain.schedule.Expense;
import com.weshare.api.v1.domain.schedule.Location;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalTime;

@ToString
@Getter
@NoArgsConstructor
public class PlaceWithDayIdDto {

    private Long dayId;
    private String title;
    private LocalTime time;
    private String memo;
    private Expense expense;
    private Location location;

    public PlaceWithDayIdDto(Long dayId, String title, LocalTime time, String memo, Expense expense, Location location) {
        this.dayId = dayId;
        this.title = title;
        this.time = time;
        this.memo = memo;
        this.expense = expense;
        this.location = location;
    }
}
