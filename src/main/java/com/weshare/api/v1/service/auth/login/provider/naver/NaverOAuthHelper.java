package com.weshare.api.v1.service.auth.login.provider.naver;

import lombok.Builder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

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

    public MultiValueMap<String, String> getTokenRequestBody(String code) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("state", state);
        body.add("code", code);
        return body;
    }

    public String getTokenUrl() {
        return tokenUrl;
    }

    public String getUserInfoUri() {
        return userInfoUri;
    }
}
