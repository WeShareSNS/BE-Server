package com.weshare.api.v1.repository.schedule.query;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weshare.api.v1.domain.schedule.*;
import com.weshare.api.v1.repository.schedule.query.dto.DayKey;
import com.weshare.api.v1.repository.schedule.query.dto.SchedulePageFlatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

import static com.weshare.api.v1.domain.schedule.QComment.comment;
import static com.weshare.api.v1.domain.schedule.QDayWithPlaceDetailsView.dayWithPlaceDetailsView;
import static com.weshare.api.v1.domain.schedule.QLike.like;
import static com.weshare.api.v1.domain.schedule.QSchedule.schedule;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SchedulePageQueryRepositoryImpl implements SchedulePageQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final ScheduleOrderSpecifierHelper orderSpecifierHelper;

    @Override
    public Page<SchedulePageFlatDto> findSchedulePage(Pageable pageable) {
        List<OrderSpecifier> orders = orderSpecifierHelper.getOrderSpecifiers(pageable);
        // count query
        final JPAQuery<Long> scheduleCountQuery = getScheduleCountQuery();
        // content query
        final List<SchedulePageFlatDto> content = getContent(pageable, orders);

        Set<Long> scheduleIds = getScheduleIds(content);
        final Map<Long, List<Day>> scheduleWithDayDetailsMap = getScheduleWithDayDetailsMap(scheduleIds);
        setDayDetails(content, scheduleWithDayDetailsMap);

        return PageableExecutionUtils.getPage(content, pageable, scheduleCountQuery::fetchOne);
    }

    private JPAQuery<Long> getScheduleCountQuery() {
        return queryFactory.select(schedule.count())
                .from(schedule);
    }

    private List<SchedulePageFlatDto> getContent(Pageable pageable, List<OrderSpecifier> orders) {
        return queryFactory.select(
                        Projections.constructor(SchedulePageFlatDto.class,
                                schedule.id,
                                schedule.title,
                                schedule.destination,
                                schedule.days.startDate,
                                schedule.days.endDate,
                                schedule.user,
                                schedule.createdDate,
                                JPAExpressions.select(like.count())
                                        .from(like)
                                        .join(like.schedule)
                                        .where(like.schedule.eq(schedule)),
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .join(comment.schedule)
                                        .where(comment.schedule.eq(schedule))
                        ))
                .from(schedule)
                .join(schedule.user)
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private Set<Long> getScheduleIds(List<SchedulePageFlatDto> content) {
        return content.stream()
                .map(SchedulePageFlatDto::getScheduleId)
                .collect(Collectors.toUnmodifiableSet());
    }

    private Map<Long, List<Day>> getScheduleWithDayDetailsMap(Set<Long> scheduleIds) {
        final List<Tuple> allDayWithPlaces = queryFactory
                .select(
                        dayWithPlaceDetailsView.dayId,
                        dayWithPlaceDetailsView.travelDate,
                        dayWithPlaceDetailsView.title,
                        dayWithPlaceDetailsView.time,
                        dayWithPlaceDetailsView.memo,
                        dayWithPlaceDetailsView.expense,
                        dayWithPlaceDetailsView.latitude,
                        dayWithPlaceDetailsView.longitude,
                        dayWithPlaceDetailsView.scheduleId
                )
                .from(dayWithPlaceDetailsView)
                .where(dayWithPlaceDetailsView.scheduleId.in(scheduleIds))
                .fetch();

        final Map<Long, Map<DayKey, List<Place>>> scheduleIdWithDayToPlaceMap = getScheduleIdWithDayToPlaceMap(allDayWithPlaces);
        return scheduleIdWithDayToPlaceMap.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, entry -> createDayDetails(entry.getValue())));
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

    private Map<Long, Map<DayKey, List<Place>>> getScheduleIdWithDayToPlaceMap(List<Tuple> allDayWithPlaces) {
        final Map<Long, Map<DayKey, List<Place>>> scheduleIdWithDayToPlaceMap = new HashMap<>();
        for (Tuple allDayWithPlace : allDayWithPlaces) {
            Long scheduleId = allDayWithPlace.get(dayWithPlaceDetailsView.scheduleId);

            DayKey dayKey = createKey(allDayWithPlace);
            Place place = createPlace(allDayWithPlace);

            Map<DayKey, List<Place>> dayToPlaceMap = scheduleIdWithDayToPlaceMap.getOrDefault(scheduleId, new HashMap<>());
            List<Place> places = dayToPlaceMap.getOrDefault(dayKey, new ArrayList<>());
            places.add(place);
            dayToPlaceMap.put(dayKey, places);
            scheduleIdWithDayToPlaceMap.put(scheduleId, dayToPlaceMap);
        }
        return scheduleIdWithDayToPlaceMap;
    }

    private DayKey createKey(Tuple allDayWithPlace) {
        return new DayKey(
                allDayWithPlace.get(dayWithPlaceDetailsView.dayId),
                allDayWithPlace.get(dayWithPlaceDetailsView.travelDate));
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

    private void setDayDetails(List<SchedulePageFlatDto> content, Map<Long, List<Day>> scheduleWithDayDetailsMap) {
        content.forEach(
                c -> c.setDays(scheduleWithDayDetailsMap.get(c.getScheduleId()))
        );
    }
}