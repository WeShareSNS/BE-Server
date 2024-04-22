package com.weshare.api.v1.repository.like;

import com.weshare.api.v1.domain.schedule.like.ScheduleLike;
import com.weshare.api.v1.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ScheduleLikeRepository extends JpaRepository<ScheduleLike, Long> {

    @Query("""
            select l from ScheduleLike l 
                join fetch l.user 
                    where l.scheduleId = :ScheduleId
            """)
    Slice<ScheduleLike> findAllLikeBySchedule(Long ScheduleId, Pageable pageable);

    Optional<ScheduleLike> findLikeByUser(User user);

    @Query("""
            select l from ScheduleLike l  
            where l.scheduleId in :scheduleIds or l.user.id = :userId
            """)
    List<ScheduleLike> findLikeByScheduleIdsAndUserId(List<Long> scheduleIds, Long userId);

    @Query("""
            select l from ScheduleLike l  
            where l.scheduleId in :scheduleIds
            """)
    List<ScheduleLike> findLikeByScheduleIds(List<Long> scheduleIds);

    void deleteByScheduleId(Long scheduleId);
}
