package com.weshare.api.v1.event.schedule.statistics;

import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleDetails;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleTotalCount;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.event.schedule.ScheduleCreatedEvent;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import com.weshare.api.v1.repository.schedule.statistics.StatisticsScheduleDetailsRepository;
import com.weshare.api.v1.repository.schedule.statistics.StatisticsScheduleTotalCountRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.event.RecordApplicationEvents;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@RecordApplicationEvents
class StatisticsScheduleEventHandlerTest extends ScheduleTestSupport {
    @Autowired
    private StatisticsScheduleEventHandler statisticsScheduleEventHandler;
    @Autowired
    private StatisticsScheduleDetailsRepository scheduleDetailsRepository;
    @Autowired
    private StatisticsScheduleTotalCountRepository scheduleTotalCountRepository;

    @Test
    @Transactional
    public void 여행일정을_저장시_통계테이블이_저장된다() {
        // given
        User user = createUserAndSave("email@test.com", "testt", "password");
        Schedule schedule = createAndSaveSchedule("title", Destination.BUSAN, user);
        ScheduleCreatedEvent scheduleCreatedEvent = new ScheduleCreatedEvent(schedule.getId(), schedule.getTotalScheduleExpense());

        StatisticsScheduleTotalCount beforeCount = scheduleTotalCountRepository.findFirstByOrderByModifiedDate()
                .orElse(new StatisticsScheduleTotalCount());
        // when
        statisticsScheduleEventHandler.scheduleCreatedEvent(scheduleCreatedEvent);
        // then
        StatisticsScheduleTotalCount afterCount = scheduleTotalCountRepository.findFirstByOrderByModifiedDate().orElseThrow();
        assertThat(afterCount.getTotalCount()).isEqualTo(beforeCount.getTotalCount() + 1);
        StatisticsScheduleDetails statisticsScheduleDetails = scheduleDetailsRepository.findByScheduleId(schedule.getId()).orElseThrow();

        assertThat(statisticsScheduleDetails.getTotalExpense()).isEqualTo(schedule.getTotalScheduleExpense());
        assertThat(statisticsScheduleDetails.getTotalViewCount()).isEqualTo(0);
        assertThat(statisticsScheduleDetails.getTotalCommentCount()).isEqualTo(0);
    }

}