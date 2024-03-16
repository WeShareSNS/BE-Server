package com.weshare.api.v1.repository.schedule.query;

import com.querydsl.jpa.impl.JPAQueryFactory;
import com.weshare.api.v1.domain.schedule.Day;
import com.weshare.api.v1.domain.schedule.Days;
import com.weshare.api.v1.domain.schedule.Schedule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static com.weshare.api.v1.domain.schedule.QDay.day;
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
        // day fetch join places
        final List<Day> allDayWithPlaces = getDaysWithPlace(dayIds);

        final LocalDate startDate = scheduleWithAllDay.getStartDate();
        final LocalDate endDate = scheduleWithAllDay.getEndDate();
        return scheduleWithAllDay.createSelfInstanceWithDays(new Days(allDayWithPlaces, startDate, endDate));
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

    private List<Day> getDaysWithPlace(Set<Long> dayIds) {
        return queryFactory
                .selectFrom(day)
                .join(day.places).fetchJoin()
                .where(day.id.in(dayIds))
                .fetch();
    }
}