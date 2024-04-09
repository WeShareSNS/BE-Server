package com.weshare.api.v1.controller.schedule.command.dto;

import com.weshare.api.v1.domain.schedule.*;
import com.weshare.api.v1.domain.user.User;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@ToString
public class UpdateScheduleDto {
    private final User user;
    private final Long scheduleId;
    private final String title;
    private final String destination;
    private final LocalDate startDate;
    private final LocalDate endDate;
    private final List<UpdateDayDto> visitDates;

    @Builder
    private UpdateScheduleDto(
            User user,
            Long scheduleId,
            String title,
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            List<UpdateDayDto> visitDates
    ) {
        this.user = user;
        this.scheduleId = scheduleId;
        this.title = title;
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.visitDates = visitDates;
    }

    public static UpdateScheduleDto of(final UpdateScheduleRequest updateScheduleRequest, User user) {
        return UpdateScheduleDto.builder()
                .user(user)
                .scheduleId(updateScheduleRequest.getScheduleId())
                .title(updateScheduleRequest.getTitle())
                .destination(updateScheduleRequest.getDestination())
                .startDate(updateScheduleRequest.getStartDate())
                .endDate(updateScheduleRequest.getEndDate())
                .visitDates(
                        updateScheduleRequest.getDayDetail()
                                .stream()
                                .map(UpdateDayDto::from)
                                .toList())
                .build();
    }

    private static class UpdateDayDto {
        private final LocalDate travelDate;
        private final List<UpdatePlaceDto> updatePlaceDtos;

        @Builder
        private UpdateDayDto(LocalDate travelDate, List<UpdatePlaceDto> visitPlaces) {
            this.travelDate = travelDate;
            this.updatePlaceDtos = visitPlaces;
        }

        private static UpdateDayDto from(final UpdateScheduleRequest.UpdateDay updateDay) {
            return UpdateDayDto.builder()
                    .travelDate(updateDay.getTravelDate())
                    .visitPlaces(
                            updateDay.getPlaces().stream()
                                    .map(UpdatePlaceDto::from)
                                    .toList())
                    .build();
        }

        private static class UpdatePlaceDto {
            private final String title;
            private final LocalTime time;
            private final String memo;
            private final long expense;
            private final Double latitude;
            private final Double longitude;

            @Builder
            private UpdatePlaceDto(String title, LocalTime time, String memo, long expense, Double latitude, Double longitude) {
                this.title = title;
                this.time = time;
                this.memo = memo;
                this.expense = expense;
                this.latitude = latitude;
                this.longitude = longitude;
            }

            private static UpdatePlaceDto from(final UpdateScheduleRequest.UpdateDay.UpdatePlace updatePlace) {
                return UpdatePlaceDto.builder()
                        .title(updatePlace.getTitle())
                        .time(updatePlace.getTime())
                        .memo(updatePlace.getMemo())
                        .expense(updatePlace.getExpense())
                        .latitude(updatePlace.getLatitude())
                        .longitude(updatePlace.getLongitude())
                        .build();
            }
        }
    }
    public Schedule toEntity() {
        return Schedule.builder()
                .id(this.scheduleId)
                .user(this.user)
                .title(this.title)
                .destination(Destination.findDestinationByNameOrElseThrow(this.destination))
                .days(new Days(
                        this.visitDates.stream().map(this::createDay).toList(),
                        this.startDate,
                        this.endDate))
                .build();
    }

    private Day createDay(UpdateDayDto updateDayDto) {
        return Day.builder()
                .travelDate(updateDayDto.travelDate)
                .places(updateDayDto.updatePlaceDtos
                        .stream()
                        .map(this::createPlace)
                        .toList()
                )
                .build();
    }

    private Place createPlace(UpdateDayDto.UpdatePlaceDto updatePlaceDto) {
        return Place.builder()
                .title(updatePlaceDto.title)
                .time(updatePlaceDto.time)
                .memo(updatePlaceDto.memo)
                .expense(new Expense(updatePlaceDto.expense))
                .location(new Location(
                        updatePlaceDto.latitude,
                        updatePlaceDto.longitude)
                )
                .build();
    }
}
