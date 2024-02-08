package com.weshare.api.v1.config.security;

import com.weshare.api.v1.service.auth.login.AuthLoginService;
import com.weshare.api.v1.service.auth.login.policy.*;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class LoginPolicyConfig {

    private final DefaultLoginPolicy defaultLoginPolicy;
    private final GoogleLoginAndJoinPolicy googleLoginAndJoinPolicy;
    private final KakaoLoginAndJoinPolicy kakaoLoginAndJoinPolicy;
    private final NaverLoginAndJoinPolicy naverLoginAndJoinPolicy;

    @Bean
    public AuthLoginService authLoginService() {
        return new AuthLoginService(getLoginPolicies());
    }

    private List<AuthLoginPolicy> getLoginPolicies() {
        return Arrays.asList(
                defaultLoginPolicy,
                kakaoLoginAndJoinPolicy,
                googleLoginAndJoinPolicy,
                naverLoginAndJoinPolicy
        );
    }
}
