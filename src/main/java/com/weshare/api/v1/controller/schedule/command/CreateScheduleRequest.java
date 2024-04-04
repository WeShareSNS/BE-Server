package com.weshare.api.v1.controller.schedule.command;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateScheduleRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String destination;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate startDate;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
    private LocalDate endDate;

    @NotNull
    @Valid
    private List<VisitDate> dayDetail;

    public List<VisitDate> getDayDetail() {
        return Collections.unmodifiableList(dayDetail);
    }

    @Builder
    private CreateScheduleRequest(String title, String destination, LocalDate startDate, LocalDate endDate, List<VisitDate> dayDetail) {
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.dayDetail = dayDetail;
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    static class VisitDate {
        @Getter
        @NotNull
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "Asia/Seoul")
        private LocalDate travelDate;
        @NotNull
        @Valid
        private List<VisitPlace> places;

        @Builder
        private VisitDate(LocalDate travelDate, List<VisitPlace> places) {
            this.travelDate = travelDate;
            this.places = places;
        }

        public List<VisitPlace> getPlaces() {
            return Collections.unmodifiableList(places);
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        static class VisitPlace {
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
            private VisitPlace(String title, LocalTime time, String memo, long expense, Double latitude, Double longitude) {
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
