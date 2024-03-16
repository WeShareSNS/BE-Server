package com.weshare.api.v1.repository.schedule;

import com.weshare.api.v1.domain.schedule.*;
import com.weshare.api.v1.domain.user.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@SpringBootTest
@ActiveProfiles("test")
public class ScheduleTestSupport {

    @PersistenceContext
    private EntityManager entityManager;

    public void createTwoScheduleAndSaveAll() {
        initSchedule();
        initLike();
        initComment();
    }


    @Transactional
    public void initSchedule() {
        User user1 = createUserAndSave("test1@asd.com", "test1", "test1");
        User user2 = createUserAndSave("test2@asd.com", "test2", "test2");

        for (int i = 1; i <= 2; i++) {
            Schedule schedule = createSchedule("제목", i);
            schedule.setUser(user1);
            if (i % 2 == 0) {
                schedule.setUser(user2);
            }
            entityManager.persist(schedule);
        }
    }

    private User createUserAndSave(String email, String name, String password) {
        User user = User.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
        entityManager.persist(user);
        return user;
    }

    private Schedule createSchedule(String title, int index) {
        return Schedule.builder()
                .title(title)
                .destination(Destination.findDestinationByName("서울"))
                .days(createDays(index))
                .build();
    }

    private Days createDays(int index) {
        List<Day> days = List.of(
                createDay(LocalDate.of(2024, 12, 3), index),
                createDay(LocalDate.of(2024, 12, 4), index),
                createDay(LocalDate.of(2024, 12, 5), index)
        );
        return new Days(days, LocalDate.of(2024, 12, 3), LocalDate.of(2024, 12, 5));
    }

    private Day createDay(LocalDate travelDate, int index) {
        return Day.builder()
                .travelDate(travelDate)
                .places(createPlaces(index))
                .build();
    }

    private List<Place> createPlaces(int index) {
        if (index % 2 == 0) {
            return List.of(
                    createPlace("지역1", "지역 1입니다", 2000),
                    createPlace("지역2", "지역 2입니다", 2000),
                    createPlace("지역3", "지역 3입니다", 2000)
            );
        }
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
    public void initLike() {
        Schedule schedule = entityManager.find(Schedule.class, 1L);
        User user = entityManager.find(User.class, 1L);
        Like like = Like.builder()
                .user(user)
                .state(LikeState.LIKE)
                .schedule(schedule)
                .build();
        entityManager.persist(like);
    }

    @Transactional
    public void initComment() {
        Schedule schedule = entityManager.find(Schedule.class, 1L);
        User user = entityManager.find(User.class, 1L);
        Comment comment = Comment.builder()
                .content("메롱")
                .user(user)
                .schedule(schedule)
                .build();

        entityManager.persist(comment);
    }
}