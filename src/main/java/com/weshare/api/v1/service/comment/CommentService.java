package com.weshare.api.v1.service.comment;

import com.weshare.api.v1.controller.comment.DeleteCommentDto;
import com.weshare.api.v1.controller.comment.dto.CreateCommentDto;
import com.weshare.api.v1.domain.comment.Comment;
import com.weshare.api.v1.domain.comment.exception.CommentNotFoundException;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.repository.comment.CommentRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    public CreateCommentResponse saveScheduleComment(CreateCommentDto createCommentDto) {
        Schedule findSchedule = scheduleRepository.findById(createCommentDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        final Comment comment = createComment(createCommentDto, findSchedule);
        Comment savedComment = commentRepository.save(comment);
        return getCreateCommentResponse(savedComment);
    }

    private Comment createComment(CreateCommentDto createCommentDto, Schedule findSchedule) {
        return Comment.builder()
                .schedule(findSchedule)
                .user(createCommentDto.user())
                .content(createCommentDto.content())
                .build();
    }

    private CreateCommentResponse getCreateCommentResponse(Comment comment) {
        return new CreateCommentResponse(
                comment.getId(),
                comment.getUser().getName(),
                comment.getContent(),
                comment.getCreatedDate()
        );
    }

    public List<FindAllCommentDto> findAllScheduleComment(Long scheduleId) {
        return commentRepository.findAllByScheduleId(scheduleId).stream()
                .map(this::createFindAllComment)
                .toList();
    }

    private FindAllCommentDto createFindAllComment(Comment comment) {
        return new FindAllCommentDto(
                comment.getId(),
                comment.getUser().getName(),
                comment.getContent(),
                comment.getCreatedDate()
        );
    }

    public void deleteScheduleComment(DeleteCommentDto deleteCommentDto) {
        Comment comment = commentRepository.findById(deleteCommentDto.commentId())
                .orElseThrow(CommentNotFoundException::new);

        if (!comment.isScheduleId(deleteCommentDto.scheduleId())) {
            throw new IllegalArgumentException("여행일정이 올바르지 않습니다.");
        }
        if (!comment.isSameUser(deleteCommentDto.user())) {
            throw new IllegalArgumentException("사용자가 올바르지 않습니다.");
        }
        commentRepository.delete(comment);
    }

}
