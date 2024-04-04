package com.weshare.api.v1.event.schedule;

import com.weshare.api.v1.domain.comment.Comment;
import com.weshare.api.v1.event.user.UserDeletedEvent;
import com.weshare.api.v1.domain.like.Like;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.repository.comment.CommentRepository;
import com.weshare.api.v1.repository.like.LikeRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScheduleEventListener {

    private final ScheduleRepository scheduleRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    @Order(Ordered.HIGHEST_PRECEDENCE)
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void deletedEvent(UserDeletedEvent deletedEvent) {
        final List<Schedule> schedules = scheduleRepository.findByUserId(deletedEvent.userId());
        final List<Long> scheduleIds = getScheduleIds(schedules);
        deleteAllCommentByScheduleIds(scheduleIds);
        deleteAllLikeByScheduleIds(scheduleIds);
        scheduleRepository.deleteAll(schedules);
    }

    private List<Long> getScheduleIds(List<Schedule> schedules) {
        return schedules.stream()
                .map(Schedule::getId)
                .toList();
    }

    private void deleteAllCommentByScheduleIds(List<Long> scheduleIds) {
        final List<Comment> commentByScheduleIds = commentRepository.findCommentByScheduleIds(scheduleIds);
        commentRepository.deleteAll(commentByScheduleIds);
    }

    private void deleteAllLikeByScheduleIds(List<Long> scheduleIds) {
        final List<Like> likeByScheduleIds = likeRepository.findLikeByScheduleIds(scheduleIds);
        likeRepository.deleteAll(likeByScheduleIds);
    }

}
