package com.weshare.api.v1.repository.comment;

import com.weshare.api.v1.domain.comment.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
