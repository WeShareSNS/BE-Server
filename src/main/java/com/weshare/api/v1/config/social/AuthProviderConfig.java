package com.weshare.api.v1.config.social;

import com.weshare.api.v1.service.auth.login.provider.*;
import com.weshare.api.v1.service.auth.login.provider.google.GoogleLoginAndJoinProvider;
import com.weshare.api.v1.service.auth.login.provider.kakao.KakaoLoginAndJoinProvider;
import com.weshare.api.v1.service.auth.login.provider.naver.NaverLoginAndJoinProvider;
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
