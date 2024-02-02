package com.weshare.api.v1.service.auth.login;

import com.weshare.api.v1.controller.auth.dto.LoginRequest;
import com.weshare.api.v1.controller.auth.dto.TokenDto;
import com.weshare.api.v1.service.auth.login.policy.AuthLoginPolicy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthLoginService {
    private final List<AuthLoginPolicy> loginPolicies;

    public AuthLoginService(List<AuthLoginPolicy> loginPolicies) {
        this.loginPolicies = loginPolicies;
    }

    public TokenDto login (LoginRequest request, Date issuedAt) {
        String providerName = getIdentityProviderName(request);
        AuthLoginPolicy loginPolicy = getLoginPolicyByProviderName(providerName);
        return loginPolicy.login(request, issuedAt);
    }

    private AuthLoginPolicy getLoginPolicyByProviderName(String providerName) {
        return loginPolicies.stream()
                .filter(policy -> policy.isIdentityProvider(providerName))
                .findAny()
                .orElseThrow(() -> {
                    throw new IllegalStateException("해당하는 인가서버가 없습니다.");
                });
    }

    private String getIdentityProviderName(LoginRequest request) {
        return Optional.ofNullable(request.getIdentityProvider())
                .orElse("default");
    }
}
