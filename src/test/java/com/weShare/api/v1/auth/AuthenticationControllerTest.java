package com.weShare.api.v1.auth;

import com.weShare.api.v1.IntegrationMvcTestSupport;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import com.weShare.api.v1.token.RefreshToken;
import com.weShare.api.v1.token.RefreshTokenRepository;
import com.weShare.api.v1.token.TokenType;
import com.weShare.api.v1.token.jwt.JwtService;
import com.weShare.api.v1.token.jwt.logout.LogoutAccessTokenFromRedis;
import com.weShare.api.v1.token.jwt.logout.LogoutAccessTokenRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

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
    void tearDown(){
        tokenRepository.deleteAllInBatch();
        userRepository.deleteAllInBatch();
    }

    @Test
    @DisplayName("사용자는 회원가입을 할 수 있다.")
    public void signup() throws Exception {
        // given
        SignupRequest request = createSignupRequest("email@asd.com", "password", LocalDate.of(1999, 9, 27));
        String content = objectMapper.writeValueAsString(request);

        // when // then
        mockMvc.perform(post(PREFIX_ENDPOINT + "/signup")

                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자는 로그인을 할 수 있다.")
    public void login() throws Exception {
        // given
        String email = "email@asd.com";
        String password = "password";
        createAndSaveUser(email, password);

        LoginRequest request = createLoginRequest(email, password);
        String content = objectMapper.writeValueAsString(request);
        // when // then
        mockMvc.perform(post(PREFIX_ENDPOINT + "/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("사용자는 refresh 토큰을 통해서 access 토큰을 발급받을 수 있다.")
    public void reissueToken() throws Exception {
        // given
        User user = createAndSaveUser("email@asd.com", "password");
        String refreshToken = jwtService.generateRefreshToken(user);
        createAndSaveToken(user, refreshToken);
        // when // then
        mockMvc.perform(post(PREFIX_ENDPOINT + "/reissue-token")
                        .header(HttpHeaders.AUTHORIZATION, TokenType.BEARER.getType() + refreshToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        String reissueRefreshToken = tokenRepository.findTokenByUser(user).get().getToken();
        Assertions.assertFalse(refreshToken.equals(reissueRefreshToken));
    }

    @Test
    @DisplayName("사용자는 logout할 수 있다.")
    public void logout() throws Exception {
        // given
        User user = createAndSaveUser("email@asd.com", "password");
        String accessToken = jwtService.generateAccessToken(user);
        // when // then
        mockMvc.perform(post(PREFIX_ENDPOINT + "/logout")
                        .header(HttpHeaders.AUTHORIZATION, TokenType.BEARER.getType() + accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        Optional<LogoutAccessTokenFromRedis> logoutToken = logoutTokenRepository.findById(accessToken);
        Assertions.assertTrue(logoutToken.isPresent());
    }

    private SignupRequest createSignupRequest(String email, String password, LocalDate birthDate) {
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

    private RefreshToken createAndSaveToken(User user, String refreshToken) {
        RefreshToken token = RefreshToken.builder()
                .user(user)
                .tokenType(TokenType.BEARER)
                .token(refreshToken)
                .build();

        return tokenRepository.save(token);
    }

}