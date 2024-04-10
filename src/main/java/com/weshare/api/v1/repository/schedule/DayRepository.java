package com.weshare.api.v1.repository.schedule;

import com.weshare.api.v1.domain.schedule.Day;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface DayRepository extends JpaRepository<Day, Long> {

    @Query("""
            select d 
            from Day d 
            where d.id in :dayIds
            """)
    List<Day> findDayByIds(List<Long> dayIds);
}
