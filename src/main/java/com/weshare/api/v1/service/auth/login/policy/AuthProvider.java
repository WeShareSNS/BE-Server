package com.weshare.api.v1.service.auth.login.policy;

import com.weshare.api.v1.domain.user.User;

import java.util.List;

public class AuthProvider {
    private final List<ExternalProvider> externalProviders;

    public AuthProvider(List<ExternalProvider> externalProviders) {
        this.externalProviders = externalProviders;
    }

    public User getAuthUserByExternalProvider(String providerName, String code) {
        ExternalProvider provider = getLoginPolicyByProviderName(providerName);
        ResponseAuthToken token = provider.getToken(code);
        String responseBody = provider.getResponseBody(token.accessToken());
        return provider.getAuthUser(responseBody);
    }

    private ExternalProvider getLoginPolicyByProviderName(String providerName) {
        return externalProviders.stream()
                .filter(provider -> provider.isIdentityProvider(providerName))
                .findAny()
                .orElseThrow(() -> {
                    throw new IllegalStateException("해당하는 인가서버가 없습니다.");
                });
    }
}
