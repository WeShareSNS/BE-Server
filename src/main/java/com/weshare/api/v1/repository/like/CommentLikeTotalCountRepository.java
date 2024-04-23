package com.weshare.api.v1.repository.like;

import com.weshare.api.v1.domain.schedule.statistics.StatisticsCommentTotalCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeTotalCountRepository extends JpaRepository<StatisticsCommentTotalCount, Long> {
    Optional<StatisticsCommentTotalCount> findByCommentId(Long commentId);
}
