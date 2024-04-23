package com.weshare.api.v1.repository.comment;

import com.weshare.api.v1.domain.schedule.comment.Comment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query(value = """
            select c from Comment c
                 join fetch c.commenter 
                     where c.scheduleId = :scheduleId and c.parentComment is null 
            """)
    Slice<Comment> findAllByScheduleId(Long scheduleId, Pageable pageable);

    @Query(value = """
            select c from Comment c
                 join fetch c.commenter 
                     where c.scheduleId = :scheduleId and c.parentComment.id = :parentId 
            """)
    Slice<Comment> findChildAllByScheduleIdAndParentId(Long scheduleId, Long parentId, Pageable pageable);

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

    Slice<Comment> findByParentComment(Comment parentComment, Pageable pageable);

    void deleteByScheduleId(Long scheduleId);

    @Modifying
    @Query("""
            delete from Comment c 
            where c.id in :commentIds
            """)
    void deleteAllByIds(List<Long> commentIds);
}
