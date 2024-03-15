package com.weshare.api.v1.repository.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DayDetailDto {
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate travelDate;
    private List<PlaceWithDayIdDto> places;

    public DayDetailDto(LocalDate travelDate) {
        this.travelDate = travelDate;
    }

    public DayDetailDto(LocalDate travelDate, List<PlaceWithDayIdDto> places) {
        this.travelDate = travelDate;
        this.places = places;
    }
}
