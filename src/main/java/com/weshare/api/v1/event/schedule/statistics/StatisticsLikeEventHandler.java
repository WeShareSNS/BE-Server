package com.weshare.api.v1.event.schedule.statistics;

import com.weshare.api.v1.domain.schedule.statistics.StatisticsCommentLikeTotalCount;
import com.weshare.api.v1.domain.schedule.statistics.StatisticsScheduleDetails;
import com.weshare.api.v1.event.schedule.CommentLikedEvent;
import com.weshare.api.v1.event.schedule.CommentUnlikedEvent;
import com.weshare.api.v1.event.schedule.ScheduleLikedEvent;
import com.weshare.api.v1.event.schedule.ScheduleUnlikedEvent;
import com.weshare.api.v1.repository.like.CommentLikeTotalCountRepository;
import com.weshare.api.v1.repository.schedule.statistics.StatisticsScheduleDetailsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class StatisticsLikeEventHandler {

    public static final int DEFAULT_TOTAL_COUNT = 0;
    private final StatisticsScheduleDetailsRepository scheduleDetailsRepository;
    private final CommentLikeTotalCountRepository commentLikeTotalCountRepository;

    @EventListener
    @Transactional
    @Async
    public void incrementScheduleLikeTotalCount(ScheduleLikedEvent likedEvent) {
        final StatisticsScheduleDetails statisticsScheduleDetails = scheduleDetailsRepository.findByScheduleId(likedEvent.scheduleId())
                .orElseThrow(StatisticsScheduleNotFound::new);

        statisticsScheduleDetails.incrementTotalLikeCount();
    }

    @EventListener
    @Transactional
    @Async
    public void decrementScheduleLikeTotalCount(ScheduleUnlikedEvent unlikedEvent) {
        final StatisticsScheduleDetails statisticsScheduleDetails = scheduleDetailsRepository.findByScheduleId(unlikedEvent.scheduleId())
                .orElseThrow(StatisticsScheduleNotFound::new);

        statisticsScheduleDetails.decrementTotalLikeCount();
    }

    @EventListener
    @Transactional
    @Async
    public void incrementCommentLikeTotalCount(CommentLikedEvent likedEvent) {
        final Long commentId = likedEvent.commentId();
        StatisticsCommentLikeTotalCount statisticsCommentLikeTotalCount = commentLikeTotalCountRepository.findByCommentId(commentId)
                .orElse(new StatisticsCommentLikeTotalCount(commentId, DEFAULT_TOTAL_COUNT));

        statisticsCommentLikeTotalCount.incrementTotalCount();
        commentLikeTotalCountRepository.save(statisticsCommentLikeTotalCount);
    }

    @EventListener
    @Transactional
    @Async
    public void decrementCommentLikeTotalCount(CommentUnlikedEvent unlikedEvent) {
        final Long commentId = unlikedEvent.commentId();
        final StatisticsCommentLikeTotalCount statisticsCommentLikeTotalCount = commentLikeTotalCountRepository.findByCommentId(commentId)
                .orElseThrow(StatisticsCommentTotalCountNotFound::new);

        statisticsCommentLikeTotalCount.decrementTotalCount();
    }
}