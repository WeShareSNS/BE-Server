package com.weshare.api.v1.token;

import com.weshare.api.IntegrationTestSupport;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;


class RefreshRefreshTokenRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @AfterEach
    void tearDown(){
        tokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @Transactional // given user객체 트랜잭션 묶어주기

    @DisplayName("사용자가 가지고 있는 토큰을 조회할 수 있다.")
    public void findTokenByUser() {
        // given
        User user = createAndSaveUser("admin@test.com", "password");
        RefreshToken refreshToken = createAndSaveToken(user, "token");
        // when
        RefreshToken findToken = tokenRepository.findTokenByUser(user).get();
        User findUser = refreshToken.getUser();
        // then
        Assertions.assertAll(
                ()->Assertions.assertEquals(refreshToken.getToken(), findToken.getToken()),
                ()->Assertions.assertEquals(refreshToken.getTokenType(), findToken.getTokenType()),
                ()->Assertions.assertEquals(user.getEmail(), findUser.getEmail()),
                ()->Assertions.assertEquals(user.getName(), findUser.getName()),
                ()->Assertions.assertEquals(user.getRole(), findUser.getRole())
        );
    }

    @Test
    @Transactional // given user객체 트랜잭션 묶어주기

    @DisplayName("refresh token을 사용해서 사용자를 조회할 수 있다.")
    public void findUserByToken() {
        // given
        User user = createAndSaveUser("admin@test.com", "hw");
        createAndSaveToken(user, "token");
        // when
        RefreshToken refreshToken = tokenRepository.findByTokenWithUser("token").get();
        User findUser = refreshToken.getUser();
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getEmail(), findUser.getEmail()),
                () -> Assertions.assertEquals(user.getName(), findUser.getName()),
                () -> Assertions.assertEquals(user.getRole(), findUser.getRole())
        );
    }

    @Test
    @Transactional // given user객체 트랜잭션 묶어주기
    @DisplayName("사용자 email을 통해서 토큰을 조회할 수 있다.")
    public void findTokenByUserEmail() {
        // given
        User user = createAndSaveUser("admin@test.com", "hw");
        RefreshToken token = createAndSaveToken(user, "token");
        // when
        RefreshToken refreshToken = tokenRepository.findTokenByUserEmail("admin@test.com").get();
        User findUser = refreshToken.getUser();
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(token.getToken(), refreshToken.getToken()),
                () -> Assertions.assertEquals(token.getTokenType(), refreshToken.getTokenType()),
                () -> Assertions.assertEquals(user.getEmail(), findUser.getEmail()),
                () -> Assertions.assertEquals(user.getName(), findUser.getName()),
                () -> Assertions.assertEquals(user.getRole(), findUser.getRole())
        );
    }

    private User createAndSaveUser(String email, String password) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name("name")
                .birthDate(LocalDate.of(1999, 9, 27))
                .role(Role.USER)
                .build();

        return userRepository.save(user);
    }

    private RefreshToken createAndSaveToken(User user, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .tokenType(TokenType.BEARER)
                .user(user)
                .build();

        return tokenRepository.save(refreshToken);
    }

}