package com.weshare.api.v1.config.social;

import com.weshare.api.v1.service.auth.login.provider.google.GoogleOAuthHelper;
import com.weshare.api.v1.service.auth.login.provider.kakao.KakaoOAuthHelper;
import com.weshare.api.v1.service.auth.login.provider.naver.NaverOAuthHelper;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({KakaoLoginProperties.class, NaverLoginProperties.class, GoogleLoginProperties.class})
public class SocialLoginConfiguration {
    private final KakaoLoginProperties kakaoLoginProperties;
    private final NaverLoginProperties naverLoginProperties;
    private final GoogleLoginProperties googleLoginProperties;

    public SocialLoginConfiguration(KakaoLoginProperties kakaoLoginProperties, NaverLoginProperties naverLoginProperties, GoogleLoginProperties googleLoginProperties) {
        this.kakaoLoginProperties = kakaoLoginProperties;
        this.naverLoginProperties = naverLoginProperties;
        this.googleLoginProperties = googleLoginProperties;
    }

    @Bean
    public KakaoOAuthHelper kakaoOAuthHelper() {
        return KakaoOAuthHelper.builder()
                .grantType(kakaoLoginProperties.getAuthorizationGrantType())
                .redirectUri(kakaoLoginProperties.getRedirectUri())
                .clientSecret(kakaoLoginProperties.getClientSecret())
                .clientId(kakaoLoginProperties.getClientId())
                .tokenUrl(kakaoLoginProperties.getTokenUri())
                .userInfoUri(kakaoLoginProperties.getUserInfoUri())
                .build();
    }

    @Bean
    public NaverOAuthHelper naverOAuthHelper() {
        return NaverOAuthHelper.builder()
                .grantType(naverLoginProperties.getAuthorizationGrantType())
                .redirectUri(naverLoginProperties.getRedirectUri())
                .clientSecret(naverLoginProperties.getClientSecret())
                .clientId(naverLoginProperties.getClientId())
                .tokenUrl(naverLoginProperties.getTokenUri())
                .userInfoUri(naverLoginProperties.getUserInfoUri())
                .state(naverLoginProperties.getState())
                .build();
    }

    @Bean
    public GoogleOAuthHelper googleOAuthHelper() {
        return GoogleOAuthHelper.builder()
                .grantType(googleLoginProperties.getAuthorizationGrantType())
                .redirectUri(googleLoginProperties.getRedirectUri())
                .clientSecret(googleLoginProperties.getClientSecret())
                .clientId(googleLoginProperties.getClientId())
                .tokenUrl(googleLoginProperties.getTokenUri())
                .userInfoUri(googleLoginProperties.getUserInfoUri())
                .build();
    }
}
