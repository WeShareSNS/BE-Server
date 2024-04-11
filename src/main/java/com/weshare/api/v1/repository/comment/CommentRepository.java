package com.weshare.api.v1.repository.comment;

import com.weshare.api.v1.domain.schedule.comment.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = """
            select c from Comment c
                 join fetch c.commenter 
                     where c.scheduleId = :scheduleId
            """)
    Slice<Comment> findAllByScheduleId(Long scheduleId, Pageable pageable);

    @Query("""
                select c from Comment c
                where c.scheduleId in :scheduleIds or c.commenter.id = :commenterId
            """)
    List<Comment> findCommentByScheduleIdsAndCommenterId(List<Long> scheduleIds, Long commenterId);

    @Query("""
                select c from Comment c
                where c.scheduleId in :scheduleIds
            """)
    List<Comment> findCommentByScheduleIds(List<Long> scheduleIds);

    void deleteByScheduleId(Long scheduleId);
}
