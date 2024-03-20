package com.weshare.api.v1.service.comment;

import com.weshare.api.v1.controller.comment.DeleteCommentDto;
import com.weshare.api.v1.controller.comment.dto.CreateCommentDto;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

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
                () -> assertTrue(createCommentResponse.commentId() != null),
                () -> assertEquals(createCommentResponse.content(), content)
        );
    }

    @Test
    @Transactional
    public void 해당하는_여행일정이_없으면_예외가_발생한다() {
        // given
        User user = createUserAndSave("test@na.com", "test", "test");
        String content = "댓글";
        Long scheduleId = 0L;
        CreateCommentDto createCommentDto = new CreateCommentDto(user, scheduleId, content);
        // when // then
        assertThatThrownBy(() -> commentService.saveScheduleComment(createCommentDto))
                .isInstanceOf(ScheduleNotFoundException.class);
    }

    @Test
    @Transactional
    public void 해당하는_여행일정_댓글을_모두_조회할_수_있다() {
        // given
        User user = createUserAndSave("test@na.com", "test", "test");
        Schedule schedule = createAndSaveSchedule("제목", Destination.BUSAN, user);
        String content = "댓글";
        CreateCommentDto createCommentDto = new CreateCommentDto(user, schedule.getId(), content);
        CreateCommentResponse createCommentResponse = commentService.saveScheduleComment(createCommentDto);
        // when
        List<FindAllCommentDto> allScheduleComment = commentService.findAllScheduleComment(schedule.getId());
        // then
        assertThat(allScheduleComment).hasSize(1)
                .extracting("commentId", "commenter", "content", "createdDate")
                .containsExactly(
                        Tuple.tuple(
                                createCommentResponse.commentId(),
                                user.getName(),
                                content,
                                createCommentResponse.createdDate()
                        )
                );
    }

    @Test
    @Transactional
    public void 해당하는_여행일정_댓글을_삭제할_수_있다() {
        // given
        User user = createUserAndSave("test@na.com", "test", "test");
        Schedule schedule = createAndSaveSchedule("제목", Destination.BUSAN, user);
        String content  = "댓글";
        CreateCommentDto createCommentDto = new CreateCommentDto(user, schedule.getId(), content);
        CreateCommentResponse createCommentResponse = commentService.saveScheduleComment(createCommentDto);
        // when
        DeleteCommentDto deleteCommentDto = new DeleteCommentDto(user, schedule.getId(), createCommentResponse.commentId());
        commentService.deleteScheduleComment(deleteCommentDto);
        // then
        List<FindAllCommentDto> allScheduleComment = commentService.findAllScheduleComment(schedule.getId());
        assertThat(allScheduleComment).isEmpty();
    }

}