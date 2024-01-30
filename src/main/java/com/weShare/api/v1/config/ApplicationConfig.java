package com.weShare.api.v1.config;

import com.weShare.api.v1.auth.login.*;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import com.weShare.api.v1.jwt.JwtService;
import com.weShare.api.v1.token.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
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
            JwtService jwtService,
            Environment environment
    ) {
        return new AuthLoginService(getLoginPolicies(userRepository, refreshTokenRepository, jwtService, environment));
    }

    private List<AuthLoginPolicy> getLoginPolicies(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService,
            Environment environment
    ) {
        return Arrays.asList(
                new DefaultLoginPolicy(userRepository, passwordEncoder(), refreshTokenRepository, jwtService),
                new KakaoLoginAndJoinPolicy(environment, userRepository, refreshTokenRepository, jwtService),
                new NaverLoginAndJoinPolicy(environment, userRepository, refreshTokenRepository, jwtService),
                new GoogleLoginAndJoinPolicy(environment, userRepository, refreshTokenRepository, jwtService)
        );
    }

}