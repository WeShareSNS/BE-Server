package com.weshare.api.v1.repository.schedule.query;


import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleDetails;
import com.weshare.api.v1.repository.schedule.query.dto.ScheduleConditionPageDto;
import com.weshare.api.v1.service.schedule.query.ScheduleSearchCondition;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface SchedulePageQueryRepository {
    Page<Schedule> findSchedulePage(ScheduleConditionPageDto scheduleConditionPageDto);
    Map<Long, StatisticsScheduleDetails> findStatisticsDetailsScheduleIdMap(List<Long> scheduleIds);
    Map<Long, Boolean> findLikedSchedulesMap(List<Long> scheduleIds, Long userId);
    Page<Schedule> searchSchedulePage(ScheduleSearchCondition searchCondition);
}
