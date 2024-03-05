package com.weshare.api.v1.controller.schedule;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Getter
public class CreateScheduleRequest {

    @NotBlank
    private final String title;

    @NotBlank
    private final String destination;

    @NotNull
    private final LocalDate startDate;

    @NotNull
    private final LocalDate endDate;

    @NotNull
    @Valid
    private final List<VisitDate> visitDates;

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

    static class VisitDate {
        @Getter
        @NotNull
        private final LocalDate travelDate;
        @NotNull
        @Valid
        private final List<VisitPlace> visitPlaces;

        @Builder
        private VisitDate(LocalDate travelDate, List<VisitPlace> visitPlaces) {
            this.travelDate = travelDate;
            this.visitPlaces = visitPlaces;
        }

        public List<VisitPlace> getVisitPlaces() {
            return Collections.unmodifiableList(visitPlaces);
        }

        @Getter
        static class VisitPlace {
            @NotBlank
            private final String title;
            @NotNull
            private final LocalTime time;

            private final String memo;

            @Min(value = 0, message = "금액은 0원 이상이어야 합니다.")
            private final long expense;

            @NotBlank
            private final String latitude;
            @NotBlank
            private final String longitude;

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
