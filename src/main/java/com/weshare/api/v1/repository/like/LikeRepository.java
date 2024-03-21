package com.weshare.api.v1.repository.like;

import com.weshare.api.v1.domain.like.Like;
import com.weshare.api.v1.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("""
            select l from Like l 
                join fetch l.user 
                join fetch l.schedule 
                    where l.schedule.id = :ScheduleId
            """)
    Slice<Like> findAllLikeBySchedule(Long ScheduleId, Pageable pageable);

    Optional<Like> findLikeByUser(User user);
}
