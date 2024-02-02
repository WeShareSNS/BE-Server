package com.weshare.api.v1.token.jwt;

import com.weshare.api.IntegrationTestSupport;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.token.exception.InvalidTokenException;
import com.weshare.api.v1.token.exception.TokenTimeOutException;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

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
        String accessToken = jwtService.generateAccessToken(user, new Date(System.nanoTime()));
        // then
        Assertions.assertFalse(accessToken.isBlank());
    }

    @Test
    @DisplayName("토큰값은 중복되지 않는다.")
    public void duplicateGenerateAccessToken() {
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        int count = 100;
        List<String> tokens = Stream.generate(() -> jwtService.generateAccessToken(user, new Date(System.nanoTime())))
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
        String refreshToken = jwtService.generateRefreshToken(user, new Date(System.nanoTime()));
        // then
        assertFalse(refreshToken.isBlank());
    }

    @Test
    @DisplayName("토큰을 통해서 사용자 이메일을 가져올 수 있다.")
    public void extractEmail() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        String accessToken = jwtService.generateAccessToken(user, new Date(System.nanoTime()));
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
    @DisplayName("만료된 토큰으로 이메일을 조회시 예외가 발생한다.")
    public void extractEmailTimeOutTest() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        String accessToken = jwtService.generateAccessToken(user, DateUtil.yesterday());
        // when // then
        assertThatThrownBy(() -> jwtService.extractEmail(accessToken))
                .isInstanceOf(TokenTimeOutException.class)
                .hasMessage("만료된 토큰 입니다.");
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 이메일을 조회시 예외가 발생한다.")
    public void extractEmailInvalidTokenTest() {
        //given
        String invalidToken = "invalid test token";
        // when // then
        assertThatThrownBy(() -> jwtService.extractEmail(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("토큰이 유효하지 않습니다.");
    }

    @Test
    @DisplayName("토큰이 유효하면 true를 반환한다.")
    public void isTokenValid() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        String accessToken = jwtService.generateAccessToken(user, new Date(System.nanoTime()));
        // when
        boolean isValid = jwtService.isTokenValid(accessToken, user);
        // then
        assertTrue(isValid);
    }

    @Test
    @DisplayName("자신의 토큰이 아니면 False를 반환한다.")
    public void isNotTokenValid() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        User another = createAndSaveUser("nono@sa.com", "qqww", "qe");
        String userToken = jwtService.generateAccessToken(user, new Date(System.nanoTime()));
        // when
        boolean isValid = jwtService.isTokenValid(userToken, another);
        // then
        Assertions.assertFalse(isValid);
    }

    @Test
    @DisplayName("만료된 토큰이면 False를 반환한다.")
    public void isTimeOutTokenValid() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        String userToken = jwtService.generateAccessToken(user, DateUtil.yesterday());
        // when
        boolean isValid = jwtService.isTokenValid(userToken, user);
        // then
        Assertions.assertFalse(isValid);
    }

    @Test
    @DisplayName("유효하지 않은 토큰이면 False를 반환한다.")
    public void isInvalidTokenTest() {
        // given
        String invalidToken = "invalid test token";
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        // when
        boolean isValid = jwtService.isTokenValid(invalidToken, user);
        // then
        Assertions.assertFalse(isValid);
    }

    @Test
    @DisplayName("토큰의 남은 시간을 알 수 있다.")
    public void getExpireTimeFromToken() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        Date now = new Date(System.nanoTime());
        String userToken = jwtService.generateAccessToken(user, now);
        // when
        long expireTimeFromToken = jwtService.getExpireTimeFromToken(userToken);
        // then
        assertTrue(now.before(new Date(now.getTime() + expireTimeFromToken)));
    }

    @Test
    @DisplayName("만료된 토큰으로 남은시간을 조회시 예외가 발생한다.")
    public void getExpireTimeFromTokenTimeOutTest() {
        // given
        User user = createAndSaveUser("email@ggg.com", "pass", "kk");
        String accessToken = jwtService.generateAccessToken(user, DateUtil.yesterday());
        // when // then
        assertThatThrownBy(() -> jwtService.getExpireTimeFromToken(accessToken))
                .isInstanceOf(TokenTimeOutException.class)
                .hasMessage("만료된 토큰 입니다.");
    }

    @Test
    @DisplayName("유효하지 않은 토큰으로 남은시간을 조회시 예외가 발생한다.")
    public void getExpireTimeFromTokenInvalidTokenTest() {
        //given
        String invalidToken = "invalid test token";
        // when // then
        assertThatThrownBy(() -> jwtService.getExpireTimeFromToken(invalidToken))
                .isInstanceOf(InvalidTokenException.class)
                .hasMessage("토큰이 유효하지 않습니다.");
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