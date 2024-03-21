package com.weshare.api.v1.service.comment;

import com.weshare.api.v1.controller.comment.dto.DeleteCommentDto;
import com.weshare.api.v1.controller.comment.dto.UpdateCommentDto;
import com.weshare.api.v1.controller.comment.dto.CreateCommentDto;
import com.weshare.api.v1.domain.comment.Comment;
import com.weshare.api.v1.domain.comment.exception.CommentNotFoundException;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.comment.CommentRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
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
        final Schedule findSchedule = scheduleRepository.findById(createCommentDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);
        final Comment comment = createComment(createCommentDto, findSchedule);

        Comment savedComment = commentRepository.save(comment);
        return getCreateCommentResponse(savedComment);
    }

    private Comment createComment(CreateCommentDto createCommentDto, Schedule findSchedule) {
        return Comment.builder()
                .schedule(findSchedule)
                .commenter(createCommentDto.commenter())
                .content(createCommentDto.content())
                .build();
    }

    private CreateCommentResponse getCreateCommentResponse(Comment comment) {
        return new CreateCommentResponse(
                comment.getId(),
                comment.getCommenter().getName(),
                comment.getContent(),
                comment.getCreatedDate()
        );
    }

    @Transactional(readOnly = true)
    public Slice<FindAllCommentDto> findAllScheduleComment(Long scheduleId, Pageable pageable) {
        Slice<Comment> comments = commentRepository.findAllByScheduleId(scheduleId, pageable);

        return comments.map(this::createFindAllComment);
    }

    private FindAllCommentDto createFindAllComment(Comment comment) {
        return new FindAllCommentDto(
                comment.getId(),
                comment.getCommenter().getName(),
                comment.getContent(),
                comment.getCreatedDate()
        );
    }

    public void updateComment(UpdateCommentDto updateCommentDto) {
        final Comment comment = commentRepository.findById(updateCommentDto.commentId())
                .orElseThrow(CommentNotFoundException::new);

        validateUserAndScheduleId(comment, updateCommentDto.scheduleId(), updateCommentDto.commenter());
        comment.updateContent(updateCommentDto.content());
    }

    private void validateUserAndScheduleId(Comment comment, Long scheduleId, User commenter) {
        if (!comment.isSameScheduleId(scheduleId)) {
            throw new IllegalArgumentException("여행일정이 올바르지 않습니다.");
        }
        if (!comment.isSameCommenter(commenter)) {
            throw new IllegalArgumentException("사용자가 올바르지 않습니다.");
        }
    }

    public void deleteScheduleComment(DeleteCommentDto deleteCommentDto) {
        final Comment comment = commentRepository.findById(deleteCommentDto.commentId())
                .orElseThrow(CommentNotFoundException::new);

        validateUserAndScheduleId(comment, deleteCommentDto.scheduleId(), deleteCommentDto.commenter());
        commentRepository.delete(comment);
    }
}
