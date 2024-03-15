package com.weshare.api.v1.repository.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @JsonIgnore
    private Long dayId;
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm:ss", timezone = "Asia/Seoul")
    private LocalTime time;
    private String memo;
    private long expense;
    private String latitude;
    private String longitude;

    public PlaceWithDayIdDto(Long dayId, String title, LocalTime time, String memo, Expense expense, Location location) {
        this.dayId = dayId;
        this.title = title;
        this.time = time;
        this.memo = memo;
        this.expense = expense.getExpense();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }
}
