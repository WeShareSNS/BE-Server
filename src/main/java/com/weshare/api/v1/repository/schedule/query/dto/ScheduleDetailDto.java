package com.weshare.api.v1.repository.schedule.query.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.weshare.api.v1.domain.schedule.Destination;
import lombok.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

@ToString
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ScheduleDetailDto {
    private Long id;
    private String title;
    private Destination destination;
    private String username;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;
    @Setter
    private List<DayDetailDto> dayDetail;

    public ScheduleDetailDto(Long id, String title, Destination destination, String username, LocalDate startDate, LocalDate endDate) {
        this.id = id;
        this.title = title;
        this.destination = destination;
        this.username = username;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public List<DayDetailDto> getDayDetail() {
        return Collections.unmodifiableList(dayDetail);
    }
}
