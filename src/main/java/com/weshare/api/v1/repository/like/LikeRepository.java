package com.weshare.api.v1.repository.like;

import com.weshare.api.v1.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
