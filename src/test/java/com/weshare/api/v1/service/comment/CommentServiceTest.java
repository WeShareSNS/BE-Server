package com.weshare.api.v1.service.comment;

import com.weshare.api.v1.controller.comment.CreateCommentDto;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentServiceTest extends ScheduleTestSupport {

    @Autowired
    private CommentService commentService;

    @Test
    @Transactional
    public void 특정_여행일정에_댓글을_남길_수_있다() {
        // given
        User user = createUserAndSave("test@na.com", "test", "test");
        Schedule schedule = createAndSaveSchedule("제목", Destination.BUSAN, user);
        String content = "댓글";
        CreateCommentDto createCommentDto = new CreateCommentDto(user, schedule.getId(), content);
        // when
        CreateCommentResponse createCommentResponse = commentService.saveScheduleComment(createCommentDto);
        // then
        assertAll(
                () -> assertEquals(createCommentResponse.username(), user.getName()),
                () -> assertEquals(createCommentResponse.scheduleId(), schedule.getId()),
                () -> assertEquals(createCommentResponse.content(), content)
        );
    }

}