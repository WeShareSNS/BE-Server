package com.weshare.api.v1.repository.schedule.query;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weshare.api.v1.domain.schedule.Day;
import com.weshare.api.v1.repository.schedule.query.dto.DayDetailDto;
import com.weshare.api.v1.repository.schedule.query.dto.PlaceWithDayIdDto;
import com.weshare.api.v1.repository.schedule.query.dto.ScheduleDetailDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.weshare.api.v1.domain.schedule.QDay.day;
import static com.weshare.api.v1.domain.schedule.QPlace.place;
import static com.weshare.api.v1.domain.schedule.QSchedule.schedule;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleDetailQueryRepositoryImpl implements ScheduleDetailQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<ScheduleDetailDto> findScheduleDetail(Long id) {
        final Map<Long, Map<Long, DayDetailDto>> multiLevelScheduleMapInDays = createMultiLevelScheduleMapInDays(id);
        final List<PlaceWithDayIdDto> daysWithPlace = getDaysWithPlace(getDayIds(multiLevelScheduleMapInDays));

        final List<ScheduleDetailDto> scheduleDetails = queryFactory.select(
                        Projections.constructor(ScheduleDetailDto.class,
                                schedule.id,
                                schedule.title,
                                schedule.destination,
                                schedule.user.name,
                                schedule.days.startDate,
                                schedule.days.endDate
                        )
                )
                .from(schedule)
                .join(schedule.user)
                .where(schedule.id.eq(id))
                .fetch();

        final Map<Long, List<DayDetailDto>> scheduleIdWithDetailsMap = getScheduleIdWithDetailsMap(multiLevelScheduleMapInDays, daysWithPlace);
        setScheduleDetails(scheduleDetails, scheduleIdWithDetailsMap);

        return scheduleDetails;
    }

    private Map<Long, Map<Long, DayDetailDto>> createMultiLevelScheduleMapInDays(Long scheduleId) {
        List<Tuple> allDay = getAllDay(scheduleId);

        return allDay.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(schedule.id),
                        tuple -> {
                            Day day = (Day) tuple.get(schedule.days.days);
                            Map<Long, DayDetailDto> innerMap = new HashMap<>();
                            innerMap.put(day.getId(), new DayDetailDto(day.getTravelDate()));
                            return innerMap;
                        },
                        (existingMap, newMap) -> {
                            existingMap.putAll(newMap);
                            return existingMap;
                        }
                ));
    }

    private List<Tuple> getAllDay(Long scheduleId) {
        return queryFactory.select(
                        schedule.id,
                        schedule.days.days
                )
                .from(schedule)
                .join(schedule.days.days)
                .where(schedule.id.eq(scheduleId))
                .fetch();
    }

    private List<Long> getDayIds(Map<Long, Map<Long, DayDetailDto>> multiLevelScheduleMapInDays) {
        return multiLevelScheduleMapInDays.values().stream()
                .flatMap(longDayMap -> longDayMap.keySet().stream())
                .toList();
    }

    private List<PlaceWithDayIdDto> getDaysWithPlace(List<Long> dayIds) {
        return queryFactory.select(
                        Projections.constructor(
                                PlaceWithDayIdDto.class,
                                day.id,
                                place.title,
                                place.time,
                                place.memo,
                                place.expense,
                                place.location
                        )
                )
                .from(day)
                .join(day.places, place)
                .where(day.id.in(dayIds))
                .fetch();
    }

    private Map<Long, List<DayDetailDto>> getScheduleIdWithDetailsMap(Map<Long, Map<Long, DayDetailDto>> multiLevelScheduleMapInDays, List<PlaceWithDayIdDto> daysWithPlace) {
        final Map<Long, List<DayDetailDto>> scheduleIdWithDetailsMap = new HashMap<>();
        multiLevelScheduleMapInDays.forEach((scheduleId, dayDetailsMap) -> {
                    final List<DayDetailDto> dayDetails = new ArrayList<>();
                    dayDetailsMap.forEach((dayId, dayDetail) -> {
                        LocalDate travelDate = dayDetail.getTravelDate();
                        List<PlaceWithDayIdDto> placeWithDayIdDtos = daysWithPlace.stream()
                                .filter(place -> Objects.equals(place.getDayId(),dayId))
                                .toList();
                        dayDetails.add(new DayDetailDto(travelDate, placeWithDayIdDtos));
                    });
                    scheduleIdWithDetailsMap.put(scheduleId, dayDetails);
                }
        );
        return Collections.unmodifiableMap(scheduleIdWithDetailsMap);
    }

    private void setScheduleDetails(List<ScheduleDetailDto> scheduleDetails, Map<Long, List<DayDetailDto>> scheduleIdWithDetailsMap) {
        scheduleDetails.forEach(
                schedule -> {
                    List<DayDetailDto> dayDetailDtos = scheduleIdWithDetailsMap.get(schedule.getId());
                    schedule.setDayDetail(dayDetailDtos);
                }
        );
    }
}
