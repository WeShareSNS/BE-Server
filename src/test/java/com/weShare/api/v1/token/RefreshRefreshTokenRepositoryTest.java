package com.weShare.api.v1.token;

import com.weShare.api.IntegrationTestSupport;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;


class RefreshRefreshTokenRepositoryTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RefreshTokenRepository tokenRepository;

    @AfterEach
    void tearDown(){
        tokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자가 가지고 있는 토큰을 조회할 수 있다.")
    public void findTokenByUser() {
        // given
        User user = createAndSaveUser("admin@test.com", "hw", Role.USER);
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
    @DisplayName("refresh token을 사용해서 사용자를 조회할 수 있다.")
    public void findUserByToken() {
        // given
        User user = createAndSaveUser("admin@test.com", "hw", Role.USER);
        createAndSaveToken(user, "token");
        // when
        User findUser = tokenRepository.findUserByToken("token").get();
        // then
        Assertions.assertAll(
                () -> Assertions.assertEquals(user.getEmail(), findUser.getEmail()),
                () -> Assertions.assertEquals(user.getName(), findUser.getName()),
                () -> Assertions.assertEquals(user.getRole(), findUser.getRole())
        );
    }

    @Test
    @DisplayName("사용자 email을 통해서 토큰을 조회할 수 있다.")
    public void findTokenByUserEmail() {
        // given
        User user = createAndSaveUser("admin@test.com", "hw", Role.USER);
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

    private User createAndSaveUser(String email, String name, Role role) {
        User user = User.builder()
                .email(email)
                .name(name)
                .role(role)
                .birthDate(LocalDate.of(1999, 9, 27))
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