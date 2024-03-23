package com.weshare.api.v1.service.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weshare.api.v1.domain.schedule.Expense;
import com.weshare.api.v1.domain.schedule.Location;
import com.weshare.api.v1.domain.schedule.Place;
import lombok.*;

import java.time.LocalTime;

@ToString
@Getter
@NoArgsConstructor
public class PlaceDetailDto {
    private String title;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a", timezone = "Asia/Seoul", locale = "en_US")
    private LocalTime time;
    private String memo;
    private long expense;
    private Double latitude;
    private Double longitude;

    @Builder(access = AccessLevel.PRIVATE)
    private PlaceDetailDto(String title, LocalTime time, String memo, Expense expense, Location location) {
        this.title = title;
        this.time = time;
        this.memo = memo;
        this.expense = expense.getExpense();
        this.latitude = location.getLatitude();
        this.longitude = location.getLongitude();
    }

    public static PlaceDetailDto from(Place place) {
        return PlaceDetailDto.builder()
                .title(place.getTitle())
                .time(place.getTime())
                .memo(place.getMemo())
                .expense(place.getExpense())
                .location(place.getLocation())
                .build();
    }
}
