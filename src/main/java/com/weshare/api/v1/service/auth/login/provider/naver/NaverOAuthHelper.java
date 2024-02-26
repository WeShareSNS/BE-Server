package com.weshare.api.v1.service.auth.login.provider.naver;

import lombok.Builder;

import java.util.List;

public class NaverOAuthHelper {
    private final String tokenUrl;
    private final String grantType;
    private final String clientId;
    private final String clientSecret;
    private final String redirectUri;
    private final String userInfoUri;
    private final String state;

    @Builder
    private NaverOAuthHelper(String tokenUrl, String grantType, String clientId, String clientSecret, String redirectUri, String userInfoUri, String state) {
        this.tokenUrl = tokenUrl;
        this.grantType = grantType;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.redirectUri = redirectUri;
        this.userInfoUri = userInfoUri;
        this.state = state;
    }

    public List<String> getProvider() {
        return List.of(
                tokenUrl,
                grantType,
                clientId,
                clientSecret,
                redirectUri,
                userInfoUri,
                state
        );
    }
}
