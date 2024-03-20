package com.weshare.api.v1.repository.comment;

import com.weshare.api.v1.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("""
            select c from Comment c 
                join fetch c.user 
                    where c.schedule.id = :scheduleId
            """)
    List<Comment> findAllByScheduleId(Long scheduleId);
}
