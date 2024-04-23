package com.weshare.api.v1.service.comment;

import com.weshare.api.v1.controller.comment.dto.*;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.comment.Comment;
import com.weshare.api.v1.domain.schedule.comment.exception.CommentNotFoundException;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsCommentLikeTotalCount;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsParentCommentTotalCount;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.event.schedule.CommentCreatedEvent;
import com.weshare.api.v1.event.schedule.CommentDeletedEvent;
import com.weshare.api.v1.repository.comment.CommentRepository;
import com.weshare.api.v1.repository.comment.CommentTotalCountRepository;
import com.weshare.api.v1.repository.like.CommentLikeTotalCountRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CommentService {

    public static final long DEFAULT_TOTAL_COUNT = 0L;

    private final ApplicationEventPublisher eventPublisher;
    private final CommentTotalCountRepository commentTotalCountRepository;
    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;
    private final CommentLikeTotalCountRepository commentLikeTotalCountRepository;

    public CreateParentCommentResponse saveScheduleParentComment(CreateParentCommentDto createParentCommentDto) {
        final Schedule findSchedule = scheduleRepository.findById(createParentCommentDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        final Comment comment = createParentComment(createParentCommentDto, findSchedule.getId());
        commentRepository.save(comment);

        eventPublisher.publishEvent(new CommentCreatedEvent(findSchedule.getId(), null));
        return createParentCommentResponse(comment);
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
        if (!parentComment.isRootComment()) {
            throw new IllegalArgumentException("대댓글에 댓글을 달 수 없습니다.");
        }

        final Comment comment = createChildComment(createChildCommentDto, parentComment, findSchedule.getId());
        commentRepository.save(comment);

        final Long parentCommentId = comment.getParentComment().orElseThrow(CommentNotFoundException::new).getId();
        CommentCreatedEvent commentCreatedEvent = new CommentCreatedEvent(findSchedule.getId(), parentCommentId);
        eventPublisher.publishEvent(commentCreatedEvent);

        return createChildCommentResponse(comment);
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
                comment.getParentComment()
                        .orElseThrow(CommentNotFoundException::new)
                        .getId(),
                comment.getCommenter().getName(),
                comment.getContent(),
                comment.getCreatedDate()
        );
    }


    @Transactional(readOnly = true)
    public Slice<FindAllParentCommentResponse> findAllScheduleParentComment(FindAllParentCommentDto parentCommentDto) {
        Slice<Comment> comments = commentRepository.findAllByScheduleId(parentCommentDto.scheduleId(), parentCommentDto.pageable());
        final List<Long> commentIds = getCommentIds(comments.getContent());

        final List<StatisticsParentCommentTotalCount> totalCountByIds = commentTotalCountRepository.findTotalCountByParentCommentIdIn(commentIds);
        final Map<Long, Long> totalCountMap = getChildTotalCountMap(totalCountByIds);

        final List<StatisticsCommentLikeTotalCount> commentLikeTotalCounts = commentLikeTotalCountRepository.findByCommentIdIn(commentIds);
        final Map<Long, Long> totalLikeMap = getTotalLikeMap(commentLikeTotalCounts);

        return comments.map(c -> createFindAllComment(c, totalCountMap, totalLikeMap, parentCommentDto.userId()));
    }

    private List<Long> getCommentIds(List<Comment> comments) {
        return comments.stream()
                .map(Comment::getId)
                .toList();
    }

    private Map<Long, Long> getChildTotalCountMap(List<StatisticsParentCommentTotalCount> totalCountByIds) {
        return totalCountByIds.stream()
                .collect(toMap(
                        StatisticsParentCommentTotalCount::getParentCommentId,
                        StatisticsParentCommentTotalCount::getTotalCount));
    }

    private Map<Long, Long> getTotalLikeMap(List<StatisticsCommentLikeTotalCount> commentLikeTotalCounts) {
        return commentLikeTotalCounts.stream()
                .collect(toMap(StatisticsCommentLikeTotalCount::getCommentId, StatisticsCommentLikeTotalCount::getLikeTotalCount));
    }

    private FindAllParentCommentResponse createFindAllComment(
            Comment comment,
            Map<Long, Long> totalCountMap,
            Map<Long, Long> totalLikeMap,
            Long userId
    ) {
        final Long parentCommentId = comment.getId();

        return new FindAllParentCommentResponse(
                parentCommentId,
                comment.getCommenter().getName(),
                comment.getContent(),
                comment.getCreatedDate(),
                totalCountMap.getOrDefault(parentCommentId, DEFAULT_TOTAL_COUNT),
                totalLikeMap.getOrDefault(comment.getId(), DEFAULT_TOTAL_COUNT),
                comment.isSameCommenter(userId)
        );
    }

    @Transactional(readOnly = true)
    public Slice<FindAllChildCommentResponse> findAllScheduleChildComment(FindAllChildCommentDto parentCommentDto) {
        Slice<Comment> comments = commentRepository.findChildAllByScheduleIdAndParentId(
                parentCommentDto.scheduleId(), parentCommentDto.parentCommentId(), parentCommentDto.pageable());
        final List<Long> commentIds = getCommentIds(comments.getContent());

        final List<StatisticsCommentLikeTotalCount> commentLikeTotalCounts = commentLikeTotalCountRepository.findByCommentIdIn(commentIds);
        final Map<Long, Long> totalLikeMap = getTotalLikeMap(commentLikeTotalCounts);

        return comments.map(c -> createFindAllChildComment(c, totalLikeMap, parentCommentDto.userId()));
    }

    private FindAllChildCommentResponse createFindAllChildComment(Comment comment, Map<Long, Long> totalLikeMap, Long commenterId) {
        return new FindAllChildCommentResponse(
                comment.getId(),
                comment.getCommenter().getName(),
                comment.getContent(),
                comment.getCreatedDate(),
                totalLikeMap.getOrDefault(comment.getId(), DEFAULT_TOTAL_COUNT),
                comment.isSameCommenter(commenterId)
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
        if (!comment.isSameCommenter(commenter.getId())) {
            throw new IllegalArgumentException("사용자가 올바르지 않습니다.");
        }
    }

    public void deleteScheduleComment(DeleteCommentDto deleteCommentDto) {
        final Comment comment = commentRepository.findById(deleteCommentDto.commentId())
                .orElseThrow(CommentNotFoundException::new);

        validateUserAndScheduleId(comment, deleteCommentDto.scheduleId(), deleteCommentDto.commenter());
        Slice<Comment> parentComments = commentRepository.findByParentComment(comment, PageRequest.of(0, 3));

        int deletedCount = 0;
        if (!parentComments.isEmpty()) {
            deletedCount = deleteChildComments(parentComments, comment);
        }

        commentRepository.delete(comment);
        publishDeletedEvent(deletedCount + 1, comment);
    }

    private int deleteChildComments(Slice<Comment> parentComments, Comment comment) {
        final List<Long> deleteCommentIds = new ArrayList<>();
        addAllDeleteCommentIds(parentComments, deleteCommentIds);

        while (parentComments.hasNext()) {
            Pageable pageRequest = parentComments.nextPageable();
            parentComments = commentRepository.findByParentComment(comment, pageRequest);
            addAllDeleteCommentIds(parentComments, deleteCommentIds);
        }

        commentRepository.deleteAllByIds(deleteCommentIds);
        return deleteCommentIds.size();
    }

    private void addAllDeleteCommentIds(Slice<Comment> parentComments, List<Long> deleteCommentIds) {
        deleteCommentIds.addAll(parentComments.getContent()
                .stream()
                .map(Comment::getId)
                .toList());
    }

    private void publishDeletedEvent(int deletedCount, Comment comment) {
        if (deletedCount > 1) {
            eventPublisher.publishEvent(new CommentDeletedEvent(
                    comment.getScheduleId(), comment.getId(), null, deletedCount));
            return;
        }
        eventPublisher.publishEvent(new CommentDeletedEvent(
                comment.getScheduleId(), comment.getId(), getCommentParentId(comment), deletedCount));
    }

    private Long getCommentParentId(Comment comment) {
        return comment.getParentComment()
                .map(Comment::getId)
                .orElse(null);
    }
}
