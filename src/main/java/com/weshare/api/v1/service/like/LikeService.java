package com.weshare.api.v1.service.like;

import com.weshare.api.v1.controller.like.dto.*;
import com.weshare.api.v1.domain.schedule.comment.Comment;
import com.weshare.api.v1.domain.schedule.comment.exception.CommentNotFoundException;
import com.weshare.api.v1.domain.schedule.like.CommentLike;
import com.weshare.api.v1.domain.schedule.like.ScheduleLike;
import com.weshare.api.v1.domain.schedule.like.exception.DuplicateLikeException;
import com.weshare.api.v1.domain.schedule.like.exception.ScheduleLikeNotFoundException;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.event.schedule.CommentLikedEvent;
import com.weshare.api.v1.event.schedule.ScheduleLikedEvent;
import com.weshare.api.v1.event.schedule.ScheduleUnlikedEvent;
import com.weshare.api.v1.repository.comment.CommentRepository;
import com.weshare.api.v1.repository.like.CommentLikeRepository;
import com.weshare.api.v1.repository.like.ScheduleLikeRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {

    private final ApplicationEventPublisher eventPublisher;
    private final CommentLikeRepository commentLikeRepository;
    private final ScheduleLikeRepository scheduleLikeRepository;
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;

    @Transactional(readOnly = true)
    public Slice<FindAllScheduleLikeDto> findAllScheduleLike(Long scheduleId, Pageable pageable) {
        final Slice<ScheduleLike> allLikeBySchedule = scheduleLikeRepository.findAllLikeBySchedule(scheduleId, pageable);

        return allLikeBySchedule.map(this::getScheduleLikeDto);
    }

    private FindAllScheduleLikeDto getScheduleLikeDto(ScheduleLike scheduleLike) {
        return new FindAllScheduleLikeDto(
                scheduleLike.getId(),
                scheduleLike.getLiker().getName(),
                scheduleLike.getCreatedDate());
    }

    public CreateScheduleLikeResponse saveScheduleLike(CreateScheduleLikeDto createScheduleLikeDto) {
        final Schedule findSchedule = scheduleRepository.findById(createScheduleLikeDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        scheduleLikeRepository.findByScheduleIdAndLiker(findSchedule.getId(), createScheduleLikeDto.liker())
                .ifPresent((like -> {
                    throw new DuplicateLikeException();}));

        final ScheduleLike scheduleLike = createScheduleLike(createScheduleLikeDto, findSchedule.getId());
        scheduleLikeRepository.save(scheduleLike);
        eventPublisher.publishEvent(new ScheduleLikedEvent(findSchedule.getId()));
        return getCreateScheduleLikeResponse(scheduleLike);
    }

    private CreateScheduleLikeResponse getCreateScheduleLikeResponse(ScheduleLike scheduleLike) {
        return new CreateScheduleLikeResponse(
                scheduleLike.getScheduleId(),
                scheduleLike.getId(),
                scheduleLike.getLiker().getName(),
                scheduleLike.getCreatedDate());
    }

    private ScheduleLike createScheduleLike(CreateScheduleLikeDto createScheduleLikeDto, Long scheduleId) {
        return ScheduleLike.builder()
                .liker(createScheduleLikeDto.liker())
                .scheduleId(scheduleId)
                .build();
    }

    public void deleteScheduleLike(DeleteScheduleLikeDto deleteScheduleLikeDto) {
        final ScheduleLike scheduleLike = scheduleLikeRepository.findById(deleteScheduleLikeDto.likeId())
                .orElseThrow(ScheduleLikeNotFoundException::new);

        if (!scheduleLike.isSameLiker(deleteScheduleLikeDto.liker().getId())) {
            throw new IllegalArgumentException("사용자가 올바르지 않습니다.");
        }
        if (!scheduleLike.isSameScheduleId(deleteScheduleLikeDto.scheduleId())) {
            throw new IllegalArgumentException("여행일정이 올바르지 않습니다.");
        }
        scheduleLikeRepository.delete(scheduleLike);
        eventPublisher.publishEvent(new ScheduleUnlikedEvent(scheduleLike.getId()));
    }

    public CreateCommentLikeResponse saveCommentLike(CreateCommentLikeDto createCommentLikeDto) {
        Comment comment = commentRepository.findById(createCommentLikeDto.commentId())
                .orElseThrow(CommentNotFoundException::new);

        commentLikeRepository.findByCommentIdAndLiker(comment.getId(), createCommentLikeDto.liker())
                .ifPresent((like -> {
                    throw new DuplicateLikeException();
                }));

        CommentLike commentLike = createCommentLike(createCommentLikeDto, comment);
        commentLikeRepository.save(commentLike);

        eventPublisher.publishEvent(new CommentLikedEvent(commentLike.getCommentId()));
        return getCreateCommentLikeResponse(commentLike);
    }

    private CommentLike createCommentLike(CreateCommentLikeDto createCommentLikeDto, Comment comment) {
        return CommentLike.builder()
                .commentId(createCommentLikeDto.commentId())
                .liker(createCommentLikeDto.liker())
                .build();
    }

    private CreateCommentLikeResponse getCreateCommentLikeResponse(CommentLike commentLike) {
        return new CreateCommentLikeResponse(
                commentLike.getCommentId(),
                commentLike.getId(),
                commentLike.getLiker().getName(),
                commentLike.getCreatedDate()
        );
    }
}
