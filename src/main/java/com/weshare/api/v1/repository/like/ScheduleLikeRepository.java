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
                join fetch l.liker 
                    where l.scheduleId = :ScheduleId
            """)
    Slice<ScheduleLike> findAllLikeBySchedule(Long ScheduleId, Pageable pageable);

    @Query("""
            select l from ScheduleLike l  
            where l.scheduleId in :scheduleIds or l.liker.id = :likerId
            """)
    List<ScheduleLike> findLikeByScheduleIdsAndLikerId(List<Long> scheduleIds, Long likerId);

    @Query("""
            select l from ScheduleLike l  
            where l.scheduleId in :scheduleIds
            """)
    List<ScheduleLike> findLikeByScheduleIds(List<Long> scheduleIds);

    void deleteByScheduleId(Long scheduleId);

    Optional<Object> findByScheduleIdAndLiker(Long scheduleId, User liker);
}
