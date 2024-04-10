package com.weshare.api.v1.repository.schedule.query;

import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleDetailRepository;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScheduleDetailRepositoryImplTest extends ScheduleTestSupport {

    @Autowired
    private ScheduleDetailRepository scheduleDetailRepository;

    @Test
    @Transactional
    public void 여행일정_상세_조회() {
        // given
        User user = createUserAndSave("test1@asd.com", "test1", "test1");
        Destination destination = Destination.SEOUL;
        String title = "제목";
        Schedule schedule = createAndSaveSchedule(title, destination, user);
        // when
        Long scheduleId = schedule.getId();
        Schedule findSchedule = scheduleDetailRepository.findScheduleDetailById(scheduleId).orElseThrow();
        // then
        assertThat(findSchedule.getId()).isEqualTo(scheduleId);
        assertThat(findSchedule.getTitle()).isEqualTo(title);
        assertThat(findSchedule.getUser()).isEqualTo(user);
    }

    @Test
    public void 존재하지_않는_여행일정_조회시_예외발생() {
        // when // then
        long scheduleId = 0L;
        assertThatThrownBy(() -> scheduleDetailRepository.findScheduleDetailById(scheduleId))
                .isInstanceOf(ScheduleNotFoundException.class);
    }

}