package com.weshare.api.v1.config;

import com.weshare.api.v1.auth.login.*;
import com.weshare.api.v1.auth.login.policy.*;
import com.weshare.api.v1.domain.user.repository.UserRepository;
import com.weshare.api.v1.jwt.JwtService;
import com.weshare.api.v1.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final UserDetailsService userDetailsService;
    private final GoogleLoginAndJoinPolicy googleLoginAndJoinPolicy;
    private final KakaoLoginAndJoinPolicy kakaoLoginAndJoinPolicy;
    private final NaverLoginAndJoinPolicy naverLoginAndJoinPolicy;

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthLoginService authLoginService(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService
    ) {
        return new AuthLoginService(getLoginPolicies(userRepository, refreshTokenRepository, jwtService));
    }

    private List<AuthLoginPolicy> getLoginPolicies(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService
    ) {
        return Arrays.asList(
                new DefaultLoginPolicy(userRepository, passwordEncoder(), refreshTokenRepository, jwtService),
                kakaoLoginAndJoinPolicy,
                googleLoginAndJoinPolicy,
                naverLoginAndJoinPolicy
        );
    }

}