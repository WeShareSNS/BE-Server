package com.weshare.api.v1.init.schedule;

import com.weshare.api.v1.domain.schedule.comment.Comment;
import com.weshare.api.v1.domain.schedule.like.Like;
import com.weshare.api.v1.domain.schedule.*;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.domain.user.User;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Profile("local")
@Component
@RequiredArgsConstructor
public class InitSchedule {

    private final InitScheduleService initScheduleService;
    private final InitCommentService initCommentService;
    private final InitLikeService initLikeService;

    @PostConstruct
    public void init() {
        initScheduleService.init();
        initCommentService.init();
        initLikeService.init();
    }

    @Component
    static class InitScheduleService {
        @PersistenceContext
        private EntityManager entityManager;
        private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        @Transactional
        public void init() {
            User user = createUserAndSave("test1@asd.com", "test1", "test1234");

            for (int i = 1; i <= 100; i++) {
                Schedule schedule = createSchedule("제목 " + i, user);
                entityManager.persist(schedule);
            }
        }

        private User createUserAndSave(String email, String name, String password) {
            User user = User.builder()
                    .email(email)
                    .name(name)
                    .password(passwordEncoder.encode(password))
                    .profileImg("profile")
                    .role(Role.USER)
                    .social(Social.DEFAULT)
                    .build();
            entityManager.persist(user);
            return user;
        }

        private Schedule createSchedule(String title, User user) {
            Schedule schedule = Schedule.builder()
                    .title(title)
                    .user(user)
                    .destination(Destination.findDestinationByNameOrElseThrow("서울"))
                    .days(createDays())
                    .build();
            schedule.initDays();
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
    static class InitCommentService {
        @PersistenceContext
        private EntityManager entityManager;

        @Transactional
        public void init() {
            User user = entityManager.find(User.class, 1L);
            for (int i = 0; i < 30; i++) {
                Schedule schedule = entityManager.find(Schedule.class, 1L);
                Comment comment = Comment.builder()
                        .commenter(user)
                        .schedule(schedule)
                        .content("메롱" + "i").build();
                entityManager.persist(comment);
            }
        }
    }

    @Component
    static class InitLikeService {
        @PersistenceContext
        private EntityManager entityManager;

        @Transactional
        public void init() {
            User user = entityManager.find(User.class, 1L);
            Schedule schedule = entityManager.find(Schedule.class, 1L);
            Like like = Like.builder()
                    .user(user)
                    .schedule(schedule)
                    .build();
            entityManager.persist(like);
        }
    }
}
