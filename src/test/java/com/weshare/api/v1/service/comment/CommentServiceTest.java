package com.weshare.api.v1.service.comment;

import com.weshare.api.v1.controller.comment.dto.*;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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
        Schedule schedule = createAndSaveSchedule("제목", Destination.GYEONGGI, user);
        String content = "댓글";
        CreateParentCommentDto createParentCommentDto = new CreateParentCommentDto(user, schedule.getId(), content);
        // when
        CreateParentCommentResponse createParentCommentResponse = commentService.saveScheduleParentComment(createParentCommentDto);
        // then
        assertAll(
                () -> assertEquals(createParentCommentResponse.commenterName(), user.getName()),
                () -> assertTrue(createParentCommentResponse.commentId() != null),
                () -> assertEquals(createParentCommentResponse.content(), content)
        );
    }

    @Test
    @Transactional
    public void 해당하는_여행일정이_없으면_예외가_발생한다() {
        // given
        User user = createUserAndSave("test@na.com", "test", "test");
        String content = "댓글";
        Long scheduleId = 0L;
        CreateParentCommentDto createParentCommentDto = new CreateParentCommentDto(user, scheduleId, content);
        // when // then
        assertThatThrownBy(() -> commentService.saveScheduleParentComment(createParentCommentDto))
                .isInstanceOf(ScheduleNotFoundException.class);
    }

    @Test
    @Transactional
    public void 해당하는_여행일정_댓글을_최신순으로_조회할_수_있다() {
        // given
        User user = createUserAndSave("test@na.com", "test", "test");
        Schedule schedule = createAndSaveSchedule("제목", Destination.GYEONGGI, user);
        String content = "댓글";
        CreateParentCommentDto createParentCommentDto = new CreateParentCommentDto(user, schedule.getId(), content);
        commentService.saveScheduleParentComment(createParentCommentDto);
        commentService.saveScheduleParentComment(createParentCommentDto);
        CreateParentCommentResponse lastComment = commentService.saveScheduleParentComment(createParentCommentDto);
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createdDate"));
        // when
        Slice<FindAllParentCommentResponse> allScheduleCommentSlice = commentService.findAllScheduleParentComment(schedule.getId(), pageRequest);
        // then
        List<FindAllParentCommentResponse> allScheduleComment = allScheduleCommentSlice.getContent();
        assertThat(allScheduleComment).hasSize(1)
                .extracting("commentId", "commenterName", "content", "createdDate")
                .containsExactly(
                        Tuple.tuple(
                                lastComment.commentId(),
                                user.getName(),
                                content,
                                lastComment.createdDate()
                        )
                );
    }

    @Test
    @Transactional
    public void 해당하는_여행일정_댓글을_삭제할_수_있다() {
        // given
        User user = createUserAndSave("test@na.com", "test", "test");
        Schedule schedule = createAndSaveSchedule("제목", Destination.JEJU, user);
        String content  = "댓글";
        CreateParentCommentDto createParentCommentDto = new CreateParentCommentDto(user, schedule.getId(), content);
        CreateParentCommentResponse createParentCommentResponse = commentService.saveScheduleParentComment(createParentCommentDto);
        // when
        DeleteCommentDto deleteCommentDto = new DeleteCommentDto(user, schedule.getId(), createParentCommentResponse.commentId());
        commentService.deleteScheduleComment(deleteCommentDto);
        // then
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<FindAllParentCommentResponse> allScheduleComment = commentService.findAllScheduleParentComment(schedule.getId(),pageRequest).getContent();
        assertThat(allScheduleComment).isEmpty();
    }

    @Test
    @Transactional
    public void 해당하는_여행일정_댓글을_수정할_수_있다() {
        // given
        User user = createUserAndSave("test@na.com", "test", "test");
        Schedule schedule = createAndSaveSchedule("제목", Destination.JEJU, user);
        CreateParentCommentDto createParentCommentDto = new CreateParentCommentDto(user, schedule.getId(), "댓글");
        CreateParentCommentResponse createParentCommentResponse = commentService.saveScheduleParentComment(createParentCommentDto);
        String updateContent = "수정한 댓글 입니다.";
        // when
        UpdateCommentDto updateCommentDto = new UpdateCommentDto(user, updateContent, schedule.getId(), createParentCommentResponse.commentId());
        commentService.updateComment(updateCommentDto);
        // then
        PageRequest pageRequest = PageRequest.of(0, 1);
        List<FindAllParentCommentResponse> allScheduleComment = commentService.findAllScheduleParentComment(schedule.getId(),pageRequest).getContent();
        assertThat(allScheduleComment).hasSize(1)
                .extracting("commentId", "commenterName", "content", "createdDate")
                .containsExactly(
                        Tuple.tuple(
                                createParentCommentResponse.commentId(),
                                user.getName(),
                                updateContent,
                                createParentCommentResponse.createdDate()
                        )
                );

    }
}