package com.weshare.api.v1.domain.schedule;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ScheduleTest {

    @Autowired
    private EntityManager em;

    @Test
    @Transactional()
    @Rollback(value = false)
    public void ScheduleTest() {
        // given
        List<Place> places = List.of(
                Place.builder()
                        .location(new Location())
                        .memo("hihi1")
                        .expense(32_000)
                        .time(LocalTime.of(12, 35))
                        .build(),
                Place.builder()
                        .location(new Location())
                        .memo("hihi2")
                        .expense(31_000)
                        .time(LocalTime.of(12, 35))
                        .build(),
                Place.builder()
                        .location(new Location())
                        .memo("hihi2")
                        .expense(31_000)
                        .time(LocalTime.of(12, 35))
                        .build()
        );

        List<Day> days = List.of(
                Day.builder()
                        .places(places)
                        .travelDate(LocalDate.of(2024, 2, 27))
                        .build(),
                Day.builder()
                        .places(places)
                        .travelDate(LocalDate.of(2024, 2, 27))
                        .build(),
                Day.builder()
                        .places(places)
                        .travelDate(LocalDate.of(2024, 2, 27))
                        .build()
        );

        Schedule schedule = Schedule.builder()
                .days(days)
                .title("경포")
                .startDate(LocalDate.of(2024, 12, 3))
                .endDate(LocalDate.of(2024, 12, 5))
                .destination(Destination.BUSAN)
                .build();
        // when

        em.persist(schedule);
        em.flush();
        em.clear();
        Schedule schedule1 = em.find(Schedule.class, schedule.getId());
        // then
        System.out.println(schedule1);

    }

}