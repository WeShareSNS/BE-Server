package com.weshare.api.v1.controller.schedule.command;

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
    private LocalDate startDate;

    @NotNull
    private LocalDate endDate;

    @NotNull
    @Valid
    private List<VisitDate> visitDates;

    public List<VisitDate> getVisitDates() {
        return Collections.unmodifiableList(visitDates);
    }

    @Builder
    private CreateScheduleRequest(String title, String destination, LocalDate startDate, LocalDate endDate, List<VisitDate> visitDates) {
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visitDates = visitDates;
    }

    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    static class VisitDate {
        @Getter
        @NotNull
        private LocalDate travelDate;
        @NotNull
        @Valid
        private List<VisitPlace> visitPlaces;

        @Builder
        private VisitDate(LocalDate travelDate, List<VisitPlace> visitPlaces) {
            this.travelDate = travelDate;
            this.visitPlaces = visitPlaces;
        }

        public List<VisitPlace> getVisitPlaces() {
            return Collections.unmodifiableList(visitPlaces);
        }

        @Getter
        @NoArgsConstructor(access = AccessLevel.PROTECTED)
        static class VisitPlace {
            @NotBlank
            private String title;
            @NotNull
            private LocalTime time;

            private String memo;

            @Min(value = 0, message = "금액은 0원 이상이어야 합니다.")
            private long expense;

            @NotBlank
            private String latitude;
            @NotBlank
            private String longitude;

            @Builder
            private VisitPlace(String title, LocalTime time, String memo, long expense, String latitude, String longitude) {
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