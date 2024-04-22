package com.weshare.api.v1.repository.comment;

import com.weshare.api.v1.domain.schedule.statistics.StatisticsParentCommentTotalCount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentTotalCountRepository extends JpaRepository<StatisticsParentCommentTotalCount, Long> {

    List<StatisticsParentCommentTotalCount> findTotalCountByParentCommentIdIn(List<Long> parentCommentIds);

    Optional<StatisticsParentCommentTotalCount> findByParentCommentId(Long parentCommentId);
}
