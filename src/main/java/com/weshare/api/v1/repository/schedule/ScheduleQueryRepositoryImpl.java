package com.weshare.api.v1.repository.schedule;

import com.querydsl.core.Tuple;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weshare.api.v1.domain.schedule.Day;
import com.weshare.api.v1.domain.schedule.Expense;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.weshare.api.v1.domain.schedule.QComment.comment;
import static com.weshare.api.v1.domain.schedule.QDay.day;
import static com.weshare.api.v1.domain.schedule.QLike.like;
import static com.weshare.api.v1.domain.schedule.QPlace.place;
import static com.weshare.api.v1.domain.schedule.QSchedule.schedule;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ScheduleQueryRepositoryImpl implements ScheduleQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final ScheduleOrderSpecifierHelper orderSpecifierHelper;

    @Override
    public Page<SchedulePageDto> getSchedulePage(Pageable pageable) {
        List<OrderSpecifier> orders = orderSpecifierHelper.getOrderSpecifiers(pageable);
        // count query
        final JPAQuery<Long> scheduleCountQuery = getScheduleCountQuery();
        // content query
        final List<SchedulePageDto> content = queryFactory.select(
                        Projections.constructor(SchedulePageDto.class,
                                schedule.id,
                                schedule.user.name,
                                JPAExpressions.select(like.count())
                                        .from(like)
                                        .join(like.schedule, schedule)
                                        .where(like.user.eq(schedule.user)),
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .join(comment.schedule, schedule)
                                        .where(comment.user.eq(schedule.user))
                        ))
                .from(schedule)
                .join(schedule.user)
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        final Map<Long, Map<Long, Day>> multiLevelScheduleMapInDays = createMultiLevelScheduleMapInDays(content);
        final Map<Long, List<Expense>> dayExpensesMap = getDayExpensesMap(multiLevelScheduleMapInDays);

        Map<Long, List<Long>> scheduleTotalPriceTransformedMap = new HashMap<>();

        multiLevelScheduleMapInDays.forEach((scheduleId, dayMap) -> {
            scheduleTotalPriceTransformedMap.put(scheduleId, new ArrayList<>());
            dayMap.forEach((dayId, day) -> {
                List<Expense> dayExpenses = dayExpensesMap.get(dayId);
                long totalExpense = dayExpenses.stream()
                        .mapToLong(Expense::getExpense)
                        .sum();
                //하루의 총 비용 더해서 일정 목록을 구하는 맵에 넣어주기
                List<Long> longs = scheduleTotalPriceTransformedMap.get(scheduleId);
                longs.add(totalExpense);
                scheduleTotalPriceTransformedMap.put(scheduleId, longs);
            });
        });

        content.forEach(
                c -> {
                    List<Long> totalDayPrice = scheduleTotalPriceTransformedMap.get(c.getScheduleId());
                    long expense = totalDayPrice.stream()
                            .reduce(Long::sum)
                            .orElseThrow();
                    c.setExpense(expense);
                }
        );

        return PageableExecutionUtils.getPage(content, pageable, scheduleCountQuery::fetchOne);
    }

    private Map<Long, List<Expense>> getDayExpensesMap(Map<Long, Map<Long, Day>> multiLevelScheduleMapInDays) {
        List<Long> dayIds = getDayIds(multiLevelScheduleMapInDays);
        List<PlaceWithDayIdDto> daysWithPlace = getDaysWithPlace(dayIds);

        return daysWithPlace.stream()
                .collect(Collectors.groupingBy(PlaceWithDayIdDto::getDayId,
                        Collectors.mapping(PlaceWithDayIdDto::getExpense, Collectors.toList())));
    }

    private List<Long> getDayIds(Map<Long, Map<Long, Day>> multiLevelScheduleMapInDays) {
        return multiLevelScheduleMapInDays.values().stream()
                .flatMap(longDayMap -> longDayMap.keySet().stream())
                .toList();
    }

    private JPAQuery<Long> getScheduleCountQuery() {
        return queryFactory.select(schedule.count())
                .from(schedule);
    }

    private List<Long> getScheduleIds(List<SchedulePageDto> content) {
        return content.stream()
                .map(SchedulePageDto::getScheduleId)
                .toList();
    }

    private List<Tuple> getAllDay(List<Long> scheduleIds) {
        return queryFactory.select(
                        schedule.id,
                        schedule.days.days
                )
                .from(schedule)
                .join(schedule.days.days)
                .where(schedule.id.in(scheduleIds))
                .fetch();
    }

    private Map<Long, Map<Long, Day>> createMultiLevelScheduleMapInDays(List<SchedulePageDto> content) {
        List<Tuple> allDay = getAllDay(getScheduleIds(content));

        Map<Long, Map<Long, Day>> collect = allDay.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(schedule.id),
                        tuple -> {
                            Day day = (Day) tuple.get(schedule.days.days);
                            Map<Long, Day> innerMap = new HashMap<>();
                            innerMap.put(day.getId(), day);
                            return innerMap;
                        },
                        (existingMap, newMap) -> {
                            existingMap.putAll(newMap);
                            return existingMap;
                        }
                ));
        return collect;
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

}