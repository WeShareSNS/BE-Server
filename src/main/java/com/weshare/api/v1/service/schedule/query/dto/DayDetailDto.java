package com.weshare.api.v1.service.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weshare.api.v1.domain.schedule.Day;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DayDetailDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate travelDate;
    private List<PlaceDetailDto> places;

    private DayDetailDto(LocalDate travelDate, List<PlaceDetailDto> places) {
        this.travelDate = travelDate;
        this.places = places;
    }

    public static DayDetailDto from(Day day) {
        return new DayDetailDto(day.getTravelDate(), createPlaceDetails(day));
    }

    private static List<PlaceDetailDto> createPlaceDetails(Day day) {
        return day.getPlaces().stream()
                .map(PlaceDetailDto::from)
                .toList();
    }

    public long getTotalDayPrice() {
        return places.stream()
                .mapToLong(PlaceDetailDto::getExpense)
                .sum();
    }
}
