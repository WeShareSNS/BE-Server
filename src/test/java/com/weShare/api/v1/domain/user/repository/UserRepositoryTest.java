package com.weShare.api.v1.domain.user.repository;

import com.weShare.api.IntegrationTestSupport;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

class UserRepositoryTest extends IntegrationTestSupport {

    @Autowired
    UserRepository userRepository;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자를 이메일로 조회할 수 있다.")
    public void UserRepositoryTest() {
        // given
        String email = "admin@test.com";
        String name = "hw";
        Role role = Role.USER;

        createAndSaveUser(email, name, role);

        // when
        User user = userRepository.findByEmail(email).get();

        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getEmail(), (email)),
                () -> Assertions.assertEquals(user.getName(), (name)),
                () -> Assertions.assertEquals(user.getRole(), (role))
        );
    }


    private User createAndSaveUser(String email, String name, Role role) {
        User user = User.builder()
                .email(email)
                .name(name)
                .role(role)
                .birthDate(LocalDate.of(1999, 9, 27))
                .build();

        return userRepository.save(user);
    }

}