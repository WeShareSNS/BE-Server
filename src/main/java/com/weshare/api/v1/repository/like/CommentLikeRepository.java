package com.weshare.api.v1.repository.like;

import com.weshare.api.v1.domain.schedule.like.CommentLike;
import com.weshare.api.v1.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    Optional<CommentLike> findByCommentIdAndLiker(Long commentId, User liker);

}
