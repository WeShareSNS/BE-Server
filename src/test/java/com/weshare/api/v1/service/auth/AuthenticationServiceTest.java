package com.weshare.api.v1.service.auth;

import com.weshare.api.IntegrationTestSupport;
import com.weshare.api.v1.controller.auth.dto.LoginRequest;
import com.weshare.api.v1.controller.auth.dto.SignupRequest;
import com.weshare.api.v1.controller.auth.dto.TokenDto;
import com.weshare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.domain.user.exception.UsernameDuplicateException;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.service.auth.login.AuthLoginService;
import com.weshare.api.v1.token.RefreshToken;
import com.weshare.api.v1.token.RefreshTokenRepository;
import com.weshare.api.v1.token.TokenType;
import com.weshare.api.v1.token.jwt.JwtService;
import com.weshare.api.v1.token.logout.LogoutAccessTokenFromRedis;
import com.weshare.api.v1.token.logout.LogoutAccessTokenRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

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
    private AuthLoginService authLoginService;
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

    @Test
    @DisplayName("사용자는 회원가입을 할 수 있다.")
    public void join() {
        // given
        String email = "test@exam.com";
        String password = "password";
        String name = "hello";
        SignupRequest request = createJoinRequest(email, name, password, "1999-09-27");
        // when
        authService.join(request);
        // then
        User findUSer = userRepository.findByEmail(email).get();
        assertAll(
                () -> assertEquals(findUSer.getEmail(), email),
                () -> assertTrue(passwordEncoder.matches(password, findUSer.getPassword())),
                () -> assertEquals(findUSer.getBirthDate(), LocalDate.parse(request.birthDate()))
        );
    }

    @Test
    @DisplayName("이메일이 중복되면 예외가 발생한다.")
    public void duplicateEmail() {
        // given
        String email = "email@asd.com";
        String name = "test1";
        String password = "password";
        createAndSaveUser(email,name, password);
        // when // then
        assertThatThrownBy(() -> authService.checkDuplicateEmailForSignup(email))
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
        String name = "test2";
        createAndSaveUser(email, name, password);
        SignupRequest request = createJoinRequest(email, name, password, birthDate);

        // when //then
        assertThatThrownBy(() -> authService.join(request))
                .isInstanceOf(EmailDuplicateException.class)
                .hasMessage(String.format("%s은 가입된 이메일 입니다.", email));
    }

    @Test
    @DisplayName("닉네임이 중복되면 예외가 발생한다.")
    public void duplicateName() {
        // given
        String email = "email@asd.com";
        String name = "exUSer";
        String password = "password";
        createAndSaveUser(email, name, password);
        // when // then
        assertThatThrownBy(() -> authService.checkDuplicateNameForSignup(name))
                .isInstanceOf(UsernameDuplicateException.class)
                .hasMessage(name + "은 가입된 닉네임 입니다.");
    }

    @Test
    public void 이미_가입된_닉네임인_경우_예외발생() {
        // given
        String email = "test@exam.com";
        String password = "password";
        String birthDate = "1999-09-27";
        String name = "test2";
        createAndSaveUser(email, name, password);
        SignupRequest request = createJoinRequest("new@test.com", name, password, birthDate);

        // when //then
        assertThatThrownBy(() -> authService.join(request))
                .isInstanceOf(UsernameDuplicateException.class)
                .hasMessage(String.format("%s은 가입된 닉네임 입니다.", name));
    }

    @Test
    @DisplayName("사용자가 로그인하면 refresh 토큰을 발급 받는다.")
    public void login_refreshToken() {
        // given
        String email = "test@exam.com";
        String password = "password";
        String name = "test3";
        User user = createAndSaveUser(email, name, password);
        LoginRequest request = createLoginRequest(email, password);
        // when
        Optional<TokenDto> response = authLoginService.login(request, new Date(System.nanoTime()));
        // then
        RefreshToken refreshToken = tokenRepository.findTokenByUser(user).get();
        assertEquals(response.get().refreshToken(), refreshToken.getToken());
    }

    @Test
    @DisplayName("사용자가 로그인하면 access 토큰을 발급 받는다.")
    public void login_accessToken() {
        // given
        String email ="test@naver.com";
        String password = "password";
        String name = "test4";
        User user = createAndSaveUser(email, name, password);
        LoginRequest request = createLoginRequest(email, password);
        // when
        Optional<TokenDto> response = authLoginService.login(request, new Date(System.nanoTime()));
        // then
        String findEmail = jwtService.extractEmail(response.get().accessToken());
        assertEquals(user.getEmail(), findEmail);
    }

    @Test
    @Transactional // given에 트랜잭션 묶기 위해서
    @DisplayName("refresh token을 통해서 accessToken을 재발행 할 수있다.")
    public void refreshToken_reissue() {
        // given
        User user = createAndSaveUser("email@naver.com", "test5", "password");
        String refreshToken = jwtService.generateRefreshToken(user, new Date(System.nanoTime()));
        createAndSaveRefreshToken(user, refreshToken);
        // when
        TokenDto response = authService.reissueToken(Optional.ofNullable(refreshToken), new Date(System.nanoTime()));
        // then
        assertTrue(jwtService.isTokenValid(response.accessToken(), user));
    }

    @Test
    @Transactional // given에 트랜잭션 묶기 위해서
    @DisplayName("refresh 토큰을 통해서 access 토큰을 재발행시 refresh 토큰을 재발행한다.")
    public void refreshToken() {
        // given
        User user = createAndSaveUser("email@naver.com", "test6","password");
        String refreshToken = jwtService.generateRefreshToken(user, new Date(System.nanoTime()));
        createAndSaveRefreshToken(user, refreshToken);
        // when
        TokenDto response = authService.reissueToken(Optional.ofNullable(refreshToken), new Date(System.nanoTime()));
        // then
        Optional<RefreshToken> userByOldToken = tokenRepository.findByTokenWithUser(refreshToken);
        Optional<RefreshToken> userByNewToken = tokenRepository.findByTokenWithUser(response.refreshToken());

        assertTrue(userByOldToken.isEmpty());
        assertTrue(userByNewToken.isPresent());
    }

    @Test
    @DisplayName("사용자는 로그아웃을 할 수 있다.")
    public void logout() {
        // given
        String email = "email@test.com";
        String password = "password";
        String name = "test7";
        User user = createAndSaveUser(email, name, password);
        String jwt = jwtService.generateAccessToken(user, new Date(System.nanoTime()));

        LoginRequest loginRequest = createLoginRequest(email, password);
        authLoginService.login(loginRequest, new Date(System.nanoTime()));
        // when
        authService.logout(jwt);
        // then
        Optional<LogoutAccessTokenFromRedis> logoutToken = logoutTokenRepository.findById(jwt);
        Optional<RefreshToken> refreshToken = tokenRepository.findTokenByUser(user);

        assertTrue(logoutToken.isPresent());
        assertTrue(refreshToken.isEmpty());
    }

    private User createAndSaveUser(String email, String name, String password) {
        User user = User.builder()
                .email(email)
                .name(name)
                .password(passwordEncoder.encode(password))
                .role(Role.USER)
                .profileImg("profile")
                .birthDate(LocalDate.of(1999, 9, 27))
                .build();

        return userRepository.save(user);
    }

    private SignupRequest createJoinRequest(String email,String name, String password, String birthDate) {
        return SignupRequest.builder()
                .email(email)
                .name(name)
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