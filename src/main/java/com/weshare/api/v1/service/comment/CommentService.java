package com.weshare.api.v1.service.comment;

import com.weshare.api.v1.controller.comment.dto.*;
import com.weshare.api.v1.domain.schedule.comment.Comment;
import com.weshare.api.v1.domain.schedule.comment.exception.CommentNotFoundException;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.comment.CommentRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    public CreateParentCommentResponse saveScheduleParentComment(CreateParentCommentDto createParentCommentDto) {
        final Schedule findSchedule = scheduleRepository.findById(createParentCommentDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);
        final Comment comment = createParentComment(createParentCommentDto, findSchedule.getId());

        Comment savedComment = commentRepository.save(comment);
        return createParentCommentResponse(savedComment);
    }

    private Comment createParentComment(CreateParentCommentDto createParentCommentDto, Long scheduleId) {
        return Comment.builder()
                .scheduleId(scheduleId)
                .commenter(createParentCommentDto.commenter())
                .content(createParentCommentDto.content())
                .build();
    }

    private CreateParentCommentResponse createParentCommentResponse(Comment comment) {
        return new CreateParentCommentResponse(
                comment.getId(),
                comment.getCommenter().getName(),
                comment.getContent(),
                comment.getCreatedDate()
        );
    }

    public CreateChildCommentResponse saveScheduleChildComment(CreateChildCommentDto createChildCommentDto) {
        final Schedule findSchedule = scheduleRepository.findById(createChildCommentDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);
        final Comment parentComment = commentRepository.findById(createChildCommentDto.parentCommentId())
                .orElseThrow(CommentNotFoundException::new);

        if (!findSchedule.isSameScheduleId(parentComment.getScheduleId())) {
            throw new IllegalArgumentException("댓글이 요청이 올바르지 않습니다.");
        }


        final Comment comment = createChildComment(createChildCommentDto, parentComment, findSchedule.getId());
        Comment savedComment = commentRepository.save(comment);
        return createChildCommentResponse(savedComment);
    }

    private Comment createChildComment(CreateChildCommentDto createChildCommentDto, Comment parentComment, Long scheduleId) {

        return Comment.childCommentBuilder()
                .scheduleId(scheduleId)
                .commenter(createChildCommentDto.commenter())
                .content(createChildCommentDto.content())
                .parentComment(parentComment)
                .childCommentBuild();
    }

    private CreateChildCommentResponse createChildCommentResponse(Comment comment) {
        return new CreateChildCommentResponse(
                comment.getId(),
                comment.getParentComment().getId(),
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
