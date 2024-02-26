package com.weshare.api.v1.service.auth.login.provider;

import com.weshare.api.v1.domain.user.User;

public interface ExternalProvider {
    ResponseAuthToken getToken(String code);

    String getResponseBody(String accessToken);

    User getAuthUser(String responseBody);

    boolean isIdentityProvider(String providerName);
}
