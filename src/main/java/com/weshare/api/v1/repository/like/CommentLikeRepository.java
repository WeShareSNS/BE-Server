package com.weshare.api.v1.repository.like;

import com.weshare.api.v1.domain.schedule.like.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

}
