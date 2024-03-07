package com.weshare.api.v1.service.auth.login;

import com.weshare.api.v1.controller.auth.dto.LoginRequest;
import com.weshare.api.v1.controller.auth.dto.TokenDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthLoginService {

    private static final String DEFAULT_PROVIDER_SERVICE_NAME ="default";

    private final ExternalAuthProviderLoginAndJoinService authProviderLoginAndJoinService;
    private final DefaultLoginService defaultLoginService;

    public Optional<TokenDto> login(LoginRequest request, Date issuedAt) {
        String providerName = getIdentityProviderName(request);
        if (DEFAULT_PROVIDER_SERVICE_NAME.equals(providerName)) {
            return defaultLoginService.login(
                    request.email(),
                    request.password(),
                    issuedAt);
        }
        return authProviderLoginAndJoinService.login(
                providerName,
                request.code(),
                issuedAt
        );
    }

    private String getIdentityProviderName(LoginRequest request) {
        return Optional.ofNullable(request.identityProvider())
                .orElse(DEFAULT_PROVIDER_SERVICE_NAME);
    }
}
