package com.weshare.api.v1.repository.like;

import com.weshare.api.v1.domain.schedule.like.Like;
import com.weshare.api.v1.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("""
            select l from Like l 
                join fetch l.user 
                    where l.scheduleId = :ScheduleId
            """)
    Slice<Like> findAllLikeBySchedule(Long ScheduleId, Pageable pageable);

    Optional<Like> findLikeByUser(User user);

    @Query("""
            select l from Like l  
            where l.scheduleId in :scheduleIds or l.user.id = :userId
            """)
    List<Like> findLikeByScheduleIdsAndUserId(List<Long> scheduleIds, Long userId);

    @Query("""
            select l from Like l  
            where l.scheduleId in :scheduleIds
            """)
    List<Like> findLikeByScheduleIds(List<Long> scheduleIds);
}
