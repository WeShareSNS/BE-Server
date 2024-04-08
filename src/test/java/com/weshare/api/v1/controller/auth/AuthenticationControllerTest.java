package com.weshare.api.v1.controller.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.weshare.api.v1.controller.IntegrationMvcTestSupport;
import com.weshare.api.v1.controller.auth.dto.LoginRequest;
import com.weshare.api.v1.controller.auth.dto.SignupRequest;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weshare.api.v1.domain.user.exception.UsernameDuplicateException;
import com.weshare.api.v1.repository.user.UserRepository;
import com.weshare.api.v1.token.RefreshToken;
import com.weshare.api.v1.token.RefreshTokenRepository;
import com.weshare.api.v1.token.TokenType;
import com.weshare.api.v1.token.jwt.JwtService;
import com.weshare.api.v1.token.logout.LogoutAccessTokenFromRedis;
import com.weshare.api.v1.token.logout.LogoutAccessTokenRedisRepository;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthenticationControllerTest extends IntegrationMvcTestSupport {

    private static final String PREFIX_ENDPOINT = "/api/v1/auth";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private RefreshTokenRepository tokenRepository;
    @Autowired
    private LogoutAccessTokenRedisRepository logoutTokenRepository;
    @Autowired
    private JwtService jwtService;

    @AfterEach
    void tearDown() {
        tokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자는 회원가입을 할 수 있다.")
    public void signup() throws Exception {
        // given
        SignupRequest request = createSignupRequest(
                "email@asd.com",
                "test",
                "password",
                "1999-09-27"
        );
        String content = getContent(request);

        // when // then
        mockMvc.perform(post(PREFIX_ENDPOINT + "/signup")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자 이메일이 중복되어 있지 않으면 200을 반환한다.")
    public void duplicateEmailOk() throws Exception {
        // given
        String email = "email@asd.com";
        // when // then
        mockMvc.perform(get(PREFIX_ENDPOINT + "/signup/duplicate-email")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자가 이메일 양식을 지키지 않으면 400을 반환한다.")
    public void duplicateEmailBadRequest() throws Exception {
        // given
        String email = "email";

        mockMvc.perform(get(PREFIX_ENDPOINT + "/signup/duplicate-email")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("올바른 이메일 형식이 아닙니다.", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("사용자 이메일이 중복 되는 경우 409을 반환한다.")
    public void duplicateEmailConflict() throws Exception {
        // given
        String email = "email@asd.com";
        String password = "password";
        String name = "test1";
        createAndSaveUser(email, name, password);

        // when // then
        mockMvc.perform(get(PREFIX_ENDPOINT + "/signup/duplicate-email")
                        .param("email", email)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof EmailDuplicateException))
                .andExpect(result -> assertEquals(email + "은 가입된 이메일 입니다.", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("사용자 닉네임이 중복되어 있지 않으면 200을 반환한다.")
    public void duplicateNameOk() throws Exception {
        // given
        String name = "test2";
        // when // then
        mockMvc.perform(get(PREFIX_ENDPOINT + "/signup/duplicate-name")
                        .param("userName", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("사용자가 닉네임 길이를 지키지 않으면 400을 반환한다.")
    public void duplicateNameBadRequest() throws Exception {
        // given
        String name = "qweasdzxcqweasdzxcqweasdzxc";

        mockMvc.perform(get(PREFIX_ENDPOINT + "/signup/duplicate-name")
                        .param("userName", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("닉네임은 2~20 글자 사이어야 합니다.", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("사용자 닉네임이 중복 되는 경우 409을 반환한다.")
    public void duplicateNameConflict() throws Exception {
        // given
        String email = "email@asd.com";
        String password = "password";
        String name = "중복된 이름 입니다.";
        createAndSaveUser(email, name, password);

        // when // then
        mockMvc.perform(get(PREFIX_ENDPOINT + "/signup/duplicate-name")
                        .param("userName", name)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof UsernameDuplicateException))
                .andExpect(result -> assertEquals(name + "은 가입된 닉네임 입니다.", result.getResolvedException().getMessage()));
    }

    @Test
    @DisplayName("사용자는 로그인을 할 수 있다.")
    public void login() throws Exception {
        // given
        String email = "email@asd.com";
        String password = "password";
        String name = "test3";
        createAndSaveUser(email, name, password);

        LoginRequest request = createLoginRequest(email, password);
        String content = getContent(request);
        // when // then
        mockMvc.perform(post(PREFIX_ENDPOINT + "/signin")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("사용자는 refresh 토큰을 통해서 access 토큰을 발급받을 수 있다.")
    public void reissueToken() throws Exception {
        // given
        String cookieName = "Refresh-Token";
        User user = createAndSaveUser("email@asd.com", "test4", "password");
        String refreshToken = jwtService.generateRefreshToken(user, new Date(System.nanoTime()));
        createAndSaveToken(user, refreshToken);
        // when // then
        mockMvc.perform(get(PREFIX_ENDPOINT + "/reissue-token")
                        .cookie(new Cookie(cookieName, refreshToken))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String reissueRefreshToken = tokenRepository.findTokenByUser(user).get().getToken();
        Assertions.assertFalse(refreshToken.equals(reissueRefreshToken));
    }

    @Test
    @DisplayName("사용자는 logout할 수 있다.")
    public void logout() throws Exception {
        // given
        User user = createAndSaveUser("email@asd.com", "test6", "password");
        String accessToken = jwtService.generateAccessToken(user, new Date(System.nanoTime()));
        // when // then
        mockMvc.perform(post(PREFIX_ENDPOINT + "/logout")
                        .header(HttpHeaders.AUTHORIZATION, TokenType.BEARER.getType() + accessToken)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        Optional<LogoutAccessTokenFromRedis> logoutToken = logoutTokenRepository.findById(accessToken);
        assertTrue(logoutToken.isPresent());
    }

    private SignupRequest createSignupRequest(String email, String name, String password, String birthDate) {
        return SignupRequest.builder()
                .email(email)
                .userName(name)
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

    private User createAndSaveUser(String email, String name, String password) {
        User user = User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .name(name)
                .birthDate(LocalDate.of(1999, 9, 27))
                .role(Role.USER)
                .profileImg("profile")
                .social(Social.DEFAULT)
                .build();

        return userRepository.save(user);
    }

    private RefreshToken createAndSaveToken(User user, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenType(TokenType.BEARER)
                .token(refreshToken)
                .build();

        return tokenRepository.save(token);
    }

    private String getContent(Object value) throws JsonProcessingException {
        return objectMapper.writeValueAsString(value);
    }
}