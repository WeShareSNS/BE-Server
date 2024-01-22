package com.weShare.api.v1.token.jwt;

import com.weShare.api.IntegrationTestSupport;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JwtServiceImplTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @AfterEach
    void tearDown() {
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자 정보를 통해서 AccessToken을 발급받을 수 있다.")
    public void generateAccessToken() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        // when
        String accessToken = jwtService.generateAccessToken(user);
        // then
        Assertions.assertFalse(accessToken.isBlank());
    }

    @Test
    @DisplayName("토큰값이 중복되는지 체크")
    public void duplicateGenerateAccessToken() {
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        int count = 100;
        List<String> tokens = Stream.generate(() -> jwtService.generateAccessToken(user))
                .limit(count)
                .distinct()
                .collect(Collectors.toList());

        assertEquals(tokens.size(), count);
    }

    @Test
    @DisplayName("사용자 정보를 통해서 RefreshToken을 발급받을 수 있다.")
    public void generateRefreshToken() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        // when
        String refreshToken = jwtService.generateRefreshToken(user);
        // then
        Assertions.assertFalse(refreshToken.isBlank());
    }

    @Test
    @DisplayName("토큰을 통해서 사용자 이메일을 가져올 수 있다.")
    public void extractEmail() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        String accessToken = jwtService.generateAccessToken(user);
        // when
        String email = jwtService.extractEmail(accessToken);
        // then
        User findUser = userRepository.findByEmail(email).get();
        Assertions.assertAll(
                () -> Assertions.assertFalse(email.isBlank()),
                () -> Assertions.assertEquals(user.getEmail(), findUser.getEmail()),
                () -> Assertions.assertEquals(user.getPassword(), findUser.getPassword()),
                () -> Assertions.assertEquals(user.getName(), findUser.getName()),
                () -> Assertions.assertEquals(user.getId(), findUser.getId()),
                () -> Assertions.assertEquals(user.getRole(), findUser.getRole()),
                () -> Assertions.assertEquals(user.getBirthDate(), findUser.getBirthDate()),
                () -> Assertions.assertEquals(user.getProfileImg(), findUser.getProfileImg())
        );
    }

    @Test
    @DisplayName("토큰이 유효하면 true를 반환한다.")
    public void isTokenValid() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        String accessToken = jwtService.generateAccessToken(user);
        // when
        boolean isValid = jwtService.isTokenValid(accessToken, user);
        // then
        Assertions.assertTrue(isValid);
    }

    @Test
    @DisplayName("자신의 토큰이 아니면 False를 반환한다.")
    public void isNotTokenValid() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        User another = createAndSaveUser("nono@sa.com", "qqww", "qe");
        String userToken = jwtService.generateAccessToken(user);
        // when
        boolean isValid = jwtService.isTokenValid(userToken, another);
        // then
        Assertions.assertFalse(isValid);
    }

    private User createAndSaveUser(String email, String password, String name) {
        User user = User.builder()
                .email(email)
                .password(password)
                .name(name)
                .birthDate(LocalDate.of(1999, 9, 27))
                .build();

        return userRepository.save(user);
    }

}