package com.weshare.api.v1.repository.like;

import com.weshare.api.v1.domain.schedule.statistics.StatisticsCommentLikeTotalCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeTotalCountRepository extends JpaRepository<StatisticsCommentLikeTotalCount, Long> {
    Optional<StatisticsCommentLikeTotalCount> findByCommentId(Long commentId);

    List<StatisticsCommentLikeTotalCount> findByCommentIdIn(List<Long> commentIds);
}
