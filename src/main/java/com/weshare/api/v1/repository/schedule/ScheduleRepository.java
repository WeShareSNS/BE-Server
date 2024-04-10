package com.weshare.api.v1.repository.schedule;

import com.weshare.api.v1.domain.schedule.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long>, ScheduleDetailRepository {

    @Query("""
    select s from Schedule s 
    where s.user.id = :userId
    """)
    List<Schedule> findByUserId(Long userId);
}
