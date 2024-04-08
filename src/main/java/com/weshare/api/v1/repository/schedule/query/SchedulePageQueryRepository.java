package com.weshare.api.v1.repository.schedule.query;


import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleDetails;
import com.weshare.api.v1.repository.schedule.query.dto.ScheduleConditionPageDto;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface SchedulePageQueryRepository {
    Page<Schedule> findSchedulePage(ScheduleConditionPageDto scheduleConditionPageDto);
    Map<Long, StatisticsScheduleDetails> findStatisticsDetailsScheduleIdMap(List<Long> scheduleIds);
    Map<Long, Boolean> findLikedSchedulesMap(List<Long> scheduleIds, Long userId);
}
