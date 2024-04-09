package com.weshare.api.v1.controller.schedule.command.dto;

import com.weshare.api.v1.domain.schedule.*;
import com.weshare.api.v1.domain.user.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
public class CreateScheduleDto {
    private final User user;
    private final String title;
    private final String destination;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<CreateDayDto> visitDates;

    @Builder
    private CreateScheduleDto(
            User user,
            String title,
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            List<CreateDayDto> visitDates
    ) {
        this.user = user;
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visitDates = visitDates;
    }

    public static CreateScheduleDto of(final CreateScheduleRequest createScheduleRequest, User user) {
        return CreateScheduleDto.builder()
                .user(user)
                .title(createScheduleRequest.getTitle())
                .destination(createScheduleRequest.getDestination())
                .startDate(createScheduleRequest.getStartDate())
                .endDate(createScheduleRequest.getEndDate())
                .visitDates(
                        createScheduleRequest.getDayDetail()
                                .stream()
                                .map(CreateDayDto::from)
                                .toList())
                .build();
    }

    private static class CreateDayDto {
        private final LocalDate travelDate;
        private final List<CreatePlaceDto> createPlaceDtos;

        @Builder
        private CreateDayDto(LocalDate travelDate, List<CreatePlaceDto> visitPlaces) {
            this.travelDate = travelDate;
            this.createPlaceDtos = visitPlaces;
        }

        private static CreateDayDto from(final CreateScheduleRequest.CreateDay createDay) {
            return CreateDayDto.builder()
                    .travelDate(createDay.getTravelDate())
                    .visitPlaces(
                            createDay.getPlaces().stream()
                                    .map(CreatePlaceDto::from)
                                    .toList())
                    .build();
        }

        private static class CreatePlaceDto {
            private final String title;
            private final LocalTime time;
            private final String memo;
            private final long expense;
            private final Double latitude;
            private final Double longitude;

            @Builder
            private CreatePlaceDto(String title, LocalTime time, String memo, long expense, Double latitude, Double longitude) {
                this.title = title;
                this.time = time;
                this.memo = memo;
                this.expense = expense;
                this.latitude = latitude;
                this.longitude = longitude;
            }

            private static CreatePlaceDto from(final CreateScheduleRequest.CreateDay.CreatePlace createPlace) {
                return CreatePlaceDto.builder()
                        .title(createPlace.getTitle())
                        .time(createPlace.getTime())
                        .memo(createPlace.getMemo())
                        .expense(createPlace.getExpense())
                        .latitude(createPlace.getLatitude())
                        .longitude(createPlace.getLongitude())
                        .build();
            }
        }
    }

    public Schedule toEntity() {
        return Schedule.builder()
                .user(this.user)
                .title(this.title)
                .destination(Destination.findDestinationByNameOrElseThrow(this.destination))
                .days(new Days(
                        this.visitDates.stream().map(this::createDay).toList(),
                        this.startDate,
                        this.endDate))
                .build();
    }

    private Day createDay(CreateDayDto createDayDto) {
        return Day.builder()
                .travelDate(createDayDto.travelDate)
                .places(createDayDto.createPlaceDtos
                        .stream()
                        .map(this::createPlace)
                        .toList()
                )
                .build();
    }

    private Place createPlace(CreateDayDto.CreatePlaceDto createPlaceDto) {
        return Place.builder()
                .title(createPlaceDto.title)
                .time(createPlaceDto.time)
                .memo(createPlaceDto.memo)
                .expense(new Expense(createPlaceDto.expense))
                .location(new Location(
                        createPlaceDto.latitude,
                        createPlaceDto.longitude)
                )
                .build();
    }
}
