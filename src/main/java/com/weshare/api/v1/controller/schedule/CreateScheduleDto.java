package com.weshare.api.v1.controller.schedule;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;

@Getter
public class CreateScheduleDto {
    private final String title;

    private final String destination;

    private final LocalDate startDate;

    private final LocalDate endDate;
    private final List<TravelDayDto> visitDates;

    @Builder
    private CreateScheduleDto(String title, String destination, LocalDate startDate, LocalDate endDate, List<TravelDayDto> visitDates) {
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visitDates = visitDates;
    }

    public static CreateScheduleDto from(final CreateScheduleRequest createScheduleRequest) {
        return CreateScheduleDto.builder()
                .title(createScheduleRequest.getTitle())
                .destination(createScheduleRequest.getDestination())
                .startDate(createScheduleRequest.getStartDate())
                .endDate(createScheduleRequest.getEndDate())
                .visitDates(
                        createScheduleRequest.getVisitDates()
                                .stream()
                                .map(TravelDayDto::from)
                                .toList())
                .build();
    }

    public List<TravelDayDto> getVisitDates() {
        return Collections.unmodifiableList(visitDates);
    }

    public static class TravelDayDto {
        @Getter
        private final LocalDate travelDate;
        private final List<VisitPlaceDto> visitPlaceDtos;

        @Builder
        private TravelDayDto(LocalDate travelDate, List<VisitPlaceDto> visitPlaces) {
            this.travelDate = travelDate;
            this.visitPlaceDtos = visitPlaces;
        }

        public static TravelDayDto from(final CreateScheduleRequest.VisitDate visitDate) {
            return TravelDayDto.builder()
                    .travelDate(visitDate.getTravelDate())
                    .visitPlaces(
                            visitDate.getVisitPlaces().stream()
                                    .map(VisitPlaceDto::from)
                                    .toList())
                    .build();
        }

        public List<VisitPlaceDto> getVisitPlaceDtos() {
            return Collections.unmodifiableList(visitPlaceDtos);
        }

        @Getter
        public static class VisitPlaceDto {
            private final String title;
            private final LocalTime time;
            private final String memo;
            private final long expense;
            private final String latitude;
            private final String longitude;

            @Builder
            private VisitPlaceDto(String title, LocalTime time, String memo, long expense, String latitude, String longitude) {
                this.title = title;
                this.time = time;
                this.memo = memo;
                this.expense = expense;
                this.latitude = latitude;
                this.longitude = longitude;
            }

            public static VisitPlaceDto from(final CreateScheduleRequest.VisitDate.VisitPlace visitPlace) {
                return VisitPlaceDto.builder()
                        .title(visitPlace.getTitle())
                        .time(visitPlace.getTime())
                        .memo(visitPlace.getMemo())
                        .expense(visitPlace.getExpense())
                        .latitude(visitPlace.getLatitude())
                        .longitude(visitPlace.getLongitude())
                        .build();
            }
        }
    }
}
