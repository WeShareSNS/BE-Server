package com.weshare.api.v1.config;

import com.weshare.api.v1.service.auth.login.policy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AuthProviderConfig {

    private final GoogleLoginAndJoinProvider googleLoginAndJoinProvider;
    private final KakaoLoginAndJoinProvider kakaoLoginAndJoinProvider;
    private final NaverLoginAndJoinProvider naverLoginAndJoinProvider;

    @Bean
    public AuthProvider authProvider() {
        return new AuthProvider(getExternalProviders());
    }

    private List<ExternalProvider> getExternalProviders() {
        return Arrays.asList(
                googleLoginAndJoinProvider,
                kakaoLoginAndJoinProvider,
                naverLoginAndJoinProvider
        );
    }
}
