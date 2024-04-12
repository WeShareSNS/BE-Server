package com.weshare.api.v1.controller.schedule.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateScheduleRequest {
    @NotNull
    private Long scheduleId;

    private String title;
    private String destination;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;

    @Valid
    private List<UpdateDay> dayDetail;

    @Builder
    private UpdateScheduleRequest(
            Long scheduleId,
            String title,
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            List<UpdateDay> dayDetail
    ) {
        this.scheduleId = scheduleId;
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayDetail = dayDetail;
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class UpdateDay {
        @NotNull
        private Long travelDateId;
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate travelDate;
        @NotNull
        @Valid
        private List<UpdatePlace> places;

        @Builder
        private UpdateDay(Long travelDateId, LocalDate travelDate, List<UpdatePlace> places) {
            this.travelDateId = travelDateId;
            this.travelDate = travelDate;
            this.places = places;
        }

        public List<UpdatePlace> getPlaces() {
            return Collections.unmodifiableList(places);
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        public static class UpdatePlace {
            @NotBlank
            private String title;
            @NotNull
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "hh:mm a", timezone = "Asia/Seoul", locale = "en_US")
            private LocalTime time;

            private String memo;

            @Min(value = 0, message = "금액은 0원 이상이어야 합니다.")
            private long expense;

            @NotNull
            private Double latitude;
            @NotNull
            private Double longitude;
            @Builder
            private UpdatePlace(String title, LocalTime time, String memo, long expense, Double latitude, Double longitude) {
                this.title = title;
                this.time = time;
                this.memo = memo;
                this.expense = expense;
                this.latitude = latitude;
                this.longitude = longitude;
            }
        }
    }
}
