package com.weshare.api.v1.controller.schedule;

import com.weshare.api.v1.domain.schedule.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

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

    public Schedule toEntity() {
        return Schedule.builder()
                .title(title)
                .destination(Destination.findDestinationByName(destination))
                .startDate(startDate)
                .endDate(endDate)
                .days(
                        new Days(
                                visitDates.stream()
                                        .map(VisitDate::toEntity)
                                        .toList()
                        ))
                .build();
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
        @NotNull
        private LocalDate travelDate;
        @NotNull
        @Valid
        private List<VisitPlace> visitPlaces;

        private VisitDate(LocalDate travelDate, List<VisitPlace> visitPlaces) {
            this.travelDate = travelDate;
            this.visitPlaces = visitPlaces;
        }

        private Day toEntity() {
            return Day.builder()
                    .travelDate(travelDate)
                    .places(
                            visitPlaces.stream()
                                    .map(VisitPlace::toEntity)
                                    .toList()
                    )
                    .build();
        }

        static class VisitPlace {
            @NotNull
            private LocalTime time;

            private String memo;

            private String expense;

            @NotBlank
            private String latitude;
            @NotBlank
            private String longitude;

            private VisitPlace(LocalTime time, String memo, String expense, String latitude, String longitude) {
                this.time = time;
                this.memo = memo;
                this.expense = expense;
                this.latitude = latitude;
                this.longitude = longitude;
            }

            private Place toEntity() {
                return Place.builder()
                        .time(time)
                        .memo(memo)
                        .expense(expense)
                        .location(new Location(latitude, longitude))
                        .build();
            }
        }
    }
}
