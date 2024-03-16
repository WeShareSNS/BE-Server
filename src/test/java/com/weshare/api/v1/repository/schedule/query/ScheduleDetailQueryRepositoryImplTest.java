package com.weshare.api.v1.repository.schedule.query;

import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class ScheduleDetailQueryRepositoryImplTest extends ScheduleTestSupport {

    @Autowired
    private ScheduleDetailQueryRepository scheduleDetailQueryRepository;

    @Test
    public void 여행일정_상세_조회() {
        // given
        createTwoScheduleAndSaveAll();
        // when
        scheduleDetailQueryRepository.findScheduleDetail(1L);
        // then
    }

}