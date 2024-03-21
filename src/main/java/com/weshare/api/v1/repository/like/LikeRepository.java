package com.weshare.api.v1.repository.like;

import com.weshare.api.v1.domain.like.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("""
            select l from Like l 
                join fetch l.user 
                join fetch l.schedule 
                    where l.schedule.id = :ScheduleId
            """)
    List<Like> findAllLikeBySchedule(Long ScheduleId);
}
