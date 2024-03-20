package com.weshare.api.v1.config;

import com.weshare.api.v1.domain.comment.Comment;
import com.weshare.api.v1.domain.like.Like;
import com.weshare.api.v1.domain.like.LikeState;
import com.weshare.api.v1.domain.schedule.*;
import com.weshare.api.v1.domain.user.User;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Profile("init")
@Component
@RequiredArgsConstructor
public class InitSchedule {

    private final InitScheduleService initScheduleService;
    private final InitLikeService initLikeService;
    private final InitCommentService initCommentService;

    @PostConstruct
    public void init() {
        initScheduleService.init();
        initLikeService.init();
        initCommentService.init();
    }

    @Component
    static class InitScheduleService {
        @PersistenceContext
        private EntityManager entityManager;

        @Transactional
        public void init() {
            User user1 = createUserAndSave("test1@asd.com", "test1", "test1");
            User user2 = createUserAndSave("test2@asd.com", "test2", "test2");

            for (int i = 1; i <= 100; i++) {
                Schedule schedule = createSchedule("제목" + i);
                schedule.setUser(user2);
                if (i % 2 == 0) {
                    schedule.setUser(user1);
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

        private Schedule createSchedule(String title) {
            return Schedule.builder()
                    .title(title)
                    .destination(Destination.findDestinationByName("서울"))
                    .days(createDays())
                    .build();
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
                    createPlace("지역2", "지역 2입니다", 2000),
                    createPlace("지역3", "지역 3입니다", 3000)
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
            return new Location(152.64, 123.67);
        }
    }

    @Component
    static class InitLikeService {
        @PersistenceContext
        private EntityManager entityManager;
        
        @Transactional
        public void init() {
            for (long i = 1; i <= 100; i++) {
                Schedule schedule = entityManager.find(Schedule.class, i);
                User user = entityManager.find(User.class, 1L);
                Like like = Like.builder()
                        .user(user)
                        .state(LikeState.LIKE)
                        .schedule(schedule)
                        .build();
                entityManager.persist(like);
            }
        }
    }

    @Component
    static class InitCommentService {
        @PersistenceContext
        private EntityManager entityManager;
        @Transactional
        public void init() {
            for (long i = 1; i <= 100; i++) {
                Schedule schedule = entityManager.find(Schedule.class, i);
                User user = entityManager.find(User.class, 1L);
                Comment comment = Comment.builder()
                        .content("메롱" + i)
                        .user(user)
                        .schedule(schedule)
                        .build();

                entityManager.persist(comment);
            }
        }
    }
}
