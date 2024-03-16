package com.weshare.api.v1.repository.schedule.query;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weshare.api.v1.domain.schedule.*;
import com.weshare.api.v1.repository.schedule.query.dto.DayKey;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.weshare.api.v1.domain.schedule.QDayWithPlaceDetailsView.dayWithPlaceDetailsView;
import static com.weshare.api.v1.domain.schedule.QSchedule.schedule;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleDetailQueryRepositoryImpl implements ScheduleDetailQueryRepository {

    private final JPAQueryFactory queryFactory;

    @Override
    public Schedule findScheduleDetail(Long id) {
        // schedule fetch join day
        final Schedule scheduleWithAllDay = getScheduleWithAllDay(id);
        final Set<Long> dayIds = getDayIds(scheduleWithAllDay);

        // place의 원시값 포장한 값타입이 안읽어와져서 tuple로 꺼내기
        final List<Tuple> allDayWithPlaces = getDaysWithPlace(dayIds);
        final List<Day> dayWithPlaceDetails = createDayWithPlaceDetails(allDayWithPlaces);

        return scheduleWithAllDay.createSelfInstanceWithDays(
                new Days(dayWithPlaceDetails,
                        scheduleWithAllDay.getStartDate(),
                        scheduleWithAllDay.getEndDate()));
    }

    private List<Day> createDayWithPlaceDetails(List<Tuple> allDayWithPlaces) {
        final Map<DayKey, List<Place>> dayIdWithPlacesMap = new HashMap<>(allDayWithPlaces.size());
        for (Tuple allDayWithPlace : allDayWithPlaces) {
            final DayKey dayKey = createKey(allDayWithPlace);
            final Place place = createPlace(allDayWithPlace);

//            dayWithPlacesMap.computeIfAbsent(dayKey, k -> new ArrayList<>()).add(place);
            final List<Place> places = dayIdWithPlacesMap.getOrDefault(dayKey, new ArrayList<>());
            places.add(place);
            dayIdWithPlacesMap.put(dayKey, places);
        }

        return createDayDetails(dayIdWithPlacesMap);
    }

    private List<Day> createDayDetails(Map<DayKey, List<Place>> dayIdWithPlacesMap) {
        return dayIdWithPlacesMap.entrySet().stream()
                .map(entry -> Day.builder()
                        .id(entry.getKey().dayId())
                        .travelDate(entry.getKey().travelDate())
                        .places(Collections.unmodifiableList(entry.getValue()))
                        .build())
                .toList();
    }

    private Place createPlace(Tuple allDayWithPlace) {
        return Place.builder()
                .title(allDayWithPlace.get(dayWithPlaceDetailsView.title))
                .time(allDayWithPlace.get(dayWithPlaceDetailsView.time))
                .memo(allDayWithPlace.get(dayWithPlaceDetailsView.memo))
                .expense(new Expense(allDayWithPlace.get(dayWithPlaceDetailsView.expense)))
                .location(new Location(allDayWithPlace.get(dayWithPlaceDetailsView.latitude),
                        allDayWithPlace.get(dayWithPlaceDetailsView.longitude)))
                .build();
    }

    private DayKey createKey(Tuple allDayWithPlace) {
        return new DayKey(
                allDayWithPlace.get(dayWithPlaceDetailsView.dayId),
                allDayWithPlace.get(dayWithPlaceDetailsView.travelDate));
    }

    private Set<Long> getDayIds(Schedule scheduleWithAllDay) {
        return scheduleWithAllDay.getDays().stream()
                .map(Day::getId)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Schedule getScheduleWithAllDay(Long scheduleId) {
        return queryFactory
                .selectFrom(schedule)
                .join(schedule.days.days).fetchJoin()
                .join(schedule.user).fetchJoin()
                .where(schedule.id.eq(scheduleId))
                .fetchOne();
    }

    // dayWithPlaceDetailsView만 select 하면 가져오면 중복된 데이터 긁어옴
    private List<Tuple> getDaysWithPlace(Set<Long> dayIds) {
        return queryFactory
                .select(
                        dayWithPlaceDetailsView.dayId,
                        dayWithPlaceDetailsView.travelDate,
                        dayWithPlaceDetailsView.title,
                        dayWithPlaceDetailsView.time,
                        dayWithPlaceDetailsView.memo,
                        dayWithPlaceDetailsView.expense,
                        dayWithPlaceDetailsView.latitude,
                        dayWithPlaceDetailsView.longitude
                )
                .from(dayWithPlaceDetailsView)
                .where(dayWithPlaceDetailsView.dayId.in(dayIds))
                .fetch();
    }
}