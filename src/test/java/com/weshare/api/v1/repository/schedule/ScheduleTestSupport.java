package com.weshare.api.v1.repository.schedule;

import com.weshare.api.v1.domain.schedule.*;
import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class ScheduleTestSupport {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public User createUserAndSave(String email, String name, String password) {
        User user = User.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
        entityManager.persist(user);
        return user;
    }

    @Transactional
    public Schedule createAndSaveSchedule(String title, Destination destination, User user) {
        Schedule schedule = Schedule.builder()
                .title(title)
                .destination(destination)
                .days(createDays())
                .build();
        schedule.setUser(user);
        entityManager.persist(schedule);
        return schedule;
    }

    private Days createDays() {
        List<Day> days = List.of(
                createDay(LocalDate.of(2024, 12, 3)),
                createDay(LocalDate.of(2024, 12, 4)),
                createDay(LocalDate.of(2024, 12, 5))
        );
        return new Days(days, LocalDate.of(2024, 12, 3), LocalDate.of(2024, 12, 5));
    }

    private Day createDay(LocalDate travelDate) {
        return Day.builder()
                .travelDate(travelDate)
                .places(createPlaces())
                .build();
    }

    private List<Place> createPlaces() {
        return List.of(
                createPlace("지역1", "지역 1입니다", 1000),
                createPlace("지역2", "지역 2입니다", 1000),
                createPlace("지역3", "지역 3입니다", 1000)
        );
    }

    private Place createPlace(String title, String memo, long expense) {
        return Place.builder()
                .title(title)
                .time(LocalTime.of(12, 00))
                .memo(memo)
                .expense(new Expense(expense))
                .location(createLocation())
                .build();
    }

    private Location createLocation() {
        return new Location("152.64", "123,67");
    }

    @Transactional
    public void createAndSaveLike(Long scheduleId, Long userId) {
        Schedule schedule = entityManager.find(Schedule.class, scheduleId);
        User user = entityManager.find(User.class, userId);
        Like like = Like.builder()
                .user(user)
                .state(LikeState.LIKE)
                .schedule(schedule)
                .build();
        entityManager.persist(like);
    }

    @Transactional
    public void createAndSaveComment(Long scheduleId, Long userId) {
        Schedule schedule = entityManager.find(Schedule.class, scheduleId);
        User user = entityManager.find(User.class, userId);
        Comment comment = Comment.builder()
                .content("메롱")
                .user(user)
                .schedule(schedule)
                .build();
        entityManager.persist(comment);
    }
}