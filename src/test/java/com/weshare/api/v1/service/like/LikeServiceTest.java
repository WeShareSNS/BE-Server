package com.weshare.api.v1.service.like;

import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class LikeServiceTest extends ScheduleTestSupport {

    @Autowired
    private LikeService likeService;

    @Test
    @Transactional
    public void 특정_게시물에_좋아요_정보를_확인할_수_있다() {
        // given
        User user = createUserAndSave("like@test.com", "like1", "like");
        Schedule schedule = createAndSaveSchedule("title", Destination.DAEGU, user);

        // when
        likeService.findAllScheduleLike(schedule.getId());

        // then
    }

}