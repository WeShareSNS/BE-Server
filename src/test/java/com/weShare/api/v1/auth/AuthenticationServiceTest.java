package com.weShare.api.v1.auth;

import com.weShare.api.IntegrationTestSupport;
import com.weShare.api.v1.auth.controller.dto.DuplicateEmailRequest;
import com.weShare.api.v1.auth.controller.dto.LoginRequest;
import com.weShare.api.v1.auth.controller.dto.SignupRequest;
import com.weShare.api.v1.auth.controller.dto.TokenDto;
import com.weShare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import com.weShare.api.v1.token.RefreshToken;
import com.weShare.api.v1.token.RefreshTokenRepository;
import com.weShare.api.v1.token.TokenType;
import com.weShare.api.v1.jwt.JwtService;
import com.weShare.api.v1.jwt.logout.LogoutAccessTokenFromRedis;
import com.weShare.api.v1.jwt.logout.LogoutAccessTokenRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

class AuthenticationServiceTest extends IntegrationTestSupport {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuthenticationService authService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RefreshTokenRepository tokenRepository;
    @Autowired
    private JwtService jwtService;
    @Autowired
    private LogoutAccessTokenRedisRepository logoutTokenRepository;

    @AfterEach
    void tearDown(){
        logoutTokenRepository.deleteAll();
        tokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    // dto에 의존하는 test 상관 없으려나,,, test때문에 빌더도 쓰는데
    @Test
    @DisplayName("사용자는 회원가입을 할 수 있다.")
    public void join() {
        // given
        String email = "test@exam.com";
        String password = "password";
        SignupRequest request = createJoinRequest(email, password, "1999-09-27");
        // when
        authService.signup(request);
        // then
        User findUSer = userRepository.findByEmail(email).get();
        assertAll(
                () -> assertEquals(findUSer.getEmail(), email),
                () -> assertTrue(passwordEncoder.matches(password, findUSer.getPassword())),
                () -> assertEquals(findUSer.getBirthDate(), LocalDate.parse(request.getBirthDate()))
        );
    }

    @Test
    @DisplayName("이메일이 중복되면 예외가 발생한다.")
    public void duplicateEmail() {
        // given
        String email = "email@asd.com";
        String password = "password";
        createAndSaveUser(email, password);
        DuplicateEmailRequest request = new DuplicateEmailRequest(email);
        // when // then
        assertThatThrownBy(() -> authService.duplicateEmail(request))
                .isInstanceOf(EmailDuplicateException.class)
                .hasMessage(email + "은 가입된 이메일 입니다.");
    }

    @Test
    @DisplayName("이미 가입된 이메일인 경우 예외가 발생한다.")
    public void AuthenticationServiceTest() {
        // given
        String email = "test@exam.com";
        String password = "password";
        String birthDate = "1999-09-27";
        createAndSaveUser(email, password);
        SignupRequest request = createJoinRequest(email, password, birthDate);

        // when //then
        assertThatThrownBy(() -> authService.signup(request))
                .isInstanceOf(EmailDuplicateException.class)
                .hasMessage(String.format("%s은 가입된 이메일 입니다.", email));
    }

    @Test
    @DisplayName("사용자가 로그인하면 refresh 토큰을 발급 받는다.")
    public void login_refreshToken() {
        // given
        String email ="test";
        String password = "pass";
        User user = createAndSaveUser(email, password);
        LoginRequest request = createLoginRequest(email, password);
        // when
        TokenDto response = authService.login(request, new Date(System.nanoTime()));
        // then
        RefreshToken refreshToken = tokenRepository.findTokenByUser(user).get();
        assertEquals(response.refreshToken(), refreshToken.getToken());
    }

    @Test
    @DisplayName("사용자가 로그인하면 access 토큰을 발급 받는다.")
    public void login_accessToken() {
        // given
        String email ="test";
        String password = "pass";
        User user = createAndSaveUser(email, password);
        LoginRequest request = createLoginRequest(email, password);
        // when
        TokenDto response = authService.login(request, new Date(System.nanoTime()));
        // then
        String findEmail = jwtService.extractEmail(response.accessToken());
        assertEquals(user.getEmail(), findEmail);
    }

    @Test
    @DisplayName("refresh token을 통해서 accessToken을 재발행 할 수있다.")
    public void refreshToken_reissue() {
        // given
        User user = createAndSaveUser("email", "password");
        String refreshToken = jwtService.generateRefreshToken(user, new Date(System.nanoTime()));
        createAndSaveRefreshToken(user, refreshToken);
        // when
        TokenDto response = authService.reissueToken(Optional.ofNullable(refreshToken), new Date(System.nanoTime()));
        // then
        assertTrue(jwtService.isTokenValid(response.accessToken(), user));
    }

    @Test
    @DisplayName("refresh 토큰을 통해서 access 토큰을 재발행시 refresh 토큰을 재발행한다.")
    public void refreshToken() {
        // given
        User user = createAndSaveUser("email", "password");
        String refreshToken = jwtService.generateRefreshToken(user, new Date(System.nanoTime()));
        createAndSaveRefreshToken(user, refreshToken);
        // when
        TokenDto response = authService.reissueToken(Optional.ofNullable(refreshToken), new Date(System.nanoTime()));
        // then
        Optional<User> userByOldToken = tokenRepository.findUserByToken(refreshToken);
        Optional<User> userByNewToken = tokenRepository.findUserByToken(response.refreshToken());

        assertTrue(userByOldToken.isEmpty());
        assertTrue(userByNewToken.isPresent());
    }

    //jwt service를 테스트할 때마다 넣어서 처리해주는 일이 생길거같은 느낌,,
    @Test
    @DisplayName("사용자는 로그아웃을 할 수 있다.")
    public void logout() {
        // given
        String email = "email@test.com";
        String password = "password";
        User user = createAndSaveUser(email, password);
        String jwt = jwtService.generateAccessToken(user, new Date(System.nanoTime()));

        LoginRequest loginRequest = createLoginRequest(email, password);
        authService.login(loginRequest, new Date(System.nanoTime()));
        // when
        authService.logout(jwt);
        // then
        Optional<LogoutAccessTokenFromRedis> logoutToken = logoutTokenRepository.findById(jwt);
        Optional<RefreshToken> refreshToken = tokenRepository.findTokenByUser(user);

        assertTrue(logoutToken.isPresent());
        assertTrue(refreshToken.isEmpty());
    }

    private User createAndSaveUser(String email, String password) {
        User user = User.builder()
                .email(email)
                .name("not null")
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .birthDate(LocalDate.of(1999, 9, 27))
                .build();

        return userRepository.save(user);
    }

    private SignupRequest createJoinRequest(String email, String password, String birthDate) {
        return SignupRequest.builder()
                .email(email)
                .password(password)
                .birthDate(birthDate)
                .build();
    }

    private LoginRequest createLoginRequest(String email, String password) {
        return LoginRequest.builder()
                .email(email)
                .password(password)
                .build();
    }

    private RefreshToken createAndSaveRefreshToken(User user, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .token(refreshToken)
                .tokenType(TokenType.BEARER)
                .user(user)
                .build();

        return tokenRepository.save(token);
    }
}