package com.weshare.api.v1.repository.schedule.query;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.like.Like;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleDetails;
import com.weshare.api.v1.repository.schedule.query.dto.ScheduleConditionPageDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static com.weshare.api.v1.domain.schedule.QSchedule.schedule;
import static com.weshare.api.v1.domain.schedule.like.QLike.like;
import static com.weshare.api.v1.domain.schedule.statistics.QStatisticsScheduleDetails.statisticsScheduleDetails;
import static com.weshare.api.v1.domain.schedule.statistics.QStatisticsScheduleTotalCount.statisticsScheduleTotalCount;
import static java.util.stream.Collectors.toMap;

@Repository
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SchedulePageQueryRepositoryImpl implements SchedulePageQueryRepository {

    private final JPAQueryFactory queryFactory;
    private final ScheduleOrderSpecifierHelper orderSpecifierHelper;

    @Override
    public Map<Long, StatisticsScheduleDetails> findStatisticsDetailsScheduleIdMap(List<Long> scheduleIds) {
        final List<StatisticsScheduleDetails> scheduleDetails = queryFactory.selectFrom(statisticsScheduleDetails)
                .where(statisticsScheduleDetails.scheduleId.in(scheduleIds))
                .fetch();

        return scheduleIds.stream()
                .collect(toMap(
                        Function.identity(),
                        id -> scheduleDetails.stream()
                                .filter(s -> s.getScheduleId().equals(id))
                                .findAny()
                                .orElse(new StatisticsScheduleDetails(id))
                ));
    }

    @Override
    public Map<Long, Boolean> findLikedSchedulesMap(final List<Long> scheduleIds, Long userId) {
        if (userId == null) {
            return scheduleIds.stream()
                    .collect(toMap(Function.identity(), user -> false));
        }

        final List<Like> likes = queryFactory.selectFrom(like)
                .where(like.schedule.id.in(scheduleIds), like.user.id.eq(userId))
                .fetch();

        return scheduleIds.stream()
                .collect(toMap(
                        Function.identity(),
                        id -> likes.stream().anyMatch(l -> l.isSameScheduleId(id))
                ));
    }

    @Override
    public Page<Schedule> findSchedulePage(ScheduleConditionPageDto scheduleConditionPageDto) {
        // count query
        final JPAQuery<Long> countQuery = getCountQuery();
        // content query
        final List<Schedule> content = getContent(scheduleConditionPageDto);
        return PageableExecutionUtils.getPage(content, scheduleConditionPageDto.getPageable(), countQuery::fetchOne);
    }

    private JPAQuery<Long> getCountQuery() {
        return queryFactory.select(statisticsScheduleTotalCount.totalCount)
                .from(statisticsScheduleTotalCount)
                .orderBy(statisticsScheduleTotalCount.modifiedDate.asc())
                .limit(1);
    }

    private List<Schedule> getContent(ScheduleConditionPageDto scheduleConditionPageDto) {
        final Pageable pageable = scheduleConditionPageDto.getPageable();
        final List<OrderSpecifier> orders = orderSpecifierHelper.getOrderSpecifiers(pageable);

        return queryFactory.selectFrom(schedule)
                .join(schedule.user).fetchJoin()
                .where(
                        destinationIn(scheduleConditionPageDto.getDestinations()),
                        titleLike(scheduleConditionPageDto.getSearchCondition()),
                        totalExpenseBetween(scheduleConditionPageDto.getExpenseCondition())
                )
                .orderBy(orders.toArray(OrderSpecifier[]::new))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();
    }

    private BooleanExpression totalExpenseBetween(ExpenseCondition expenseCondition) {
        if (expenseCondition.isNotCondition()) {
            return null;
        }
        return schedule.id.in(
                JPAExpressions.select(statisticsScheduleDetails.scheduleId)
                        .from(statisticsScheduleDetails)
                        .where(statisticsScheduleDetails.totalExpense.between(
                                expenseCondition.minExpense(),
                                expenseCondition.maxExpense()
                        )));
    }

    private BooleanExpression titleLike(SearchCondition searchCondition) {
        if (!StringUtils.hasText(searchCondition.search())) {
            return null;
        }
        return schedule.title.like("%" + searchCondition.search() + "%");
    }

    private BooleanExpression destinationIn(List<Destination> destinations) {
        if (destinations.contains(Destination.EMPTY)) {
            return null;
        }
        return schedule.destination.in(destinations);
    }

}