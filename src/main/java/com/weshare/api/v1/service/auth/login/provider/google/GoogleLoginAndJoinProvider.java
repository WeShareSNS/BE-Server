package com.weshare.api.v1.service.auth.login.provider.google;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weshare.api.v1.common.CustomUUID;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.service.auth.login.OAuthApiException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.auth.login.provider.AuthNameGenerator;
import com.weshare.api.v1.service.auth.login.provider.ExternalProvider;
import com.weshare.api.v1.service.auth.login.provider.ResponseAuthToken;
import com.weshare.api.v1.token.TokenType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.weshare.api.v1.domain.user.Social.GOOGLE;

@Component
public class GoogleLoginAndJoinProvider implements ExternalProvider {

    private final GoogleOAuthHelper googleOAuthHelper;

    public GoogleLoginAndJoinProvider(GoogleOAuthHelper googleOAuthHelper) {
        this.googleOAuthHelper = googleOAuthHelper;
    }

    @Override
    public boolean isIdentityProvider(String providerName) {
        return GOOGLE.getProviderName().equals(providerName);
    }

    @Override
    public ResponseAuthToken getToken(String code) {
        String tokenUrl = googleOAuthHelper.getTokenUrl();
        var requestBody = googleOAuthHelper.getTokenRequestBody(code);
        RestClient restClient = RestClient.create(tokenUrl);

        return restClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(requestBody)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new OAuthApiException(response.getStatusCode(), response.getHeaders());
                })
                .toEntity(ResponseAuthToken.class)
                .getBody();
    }

    @Override
    public String getResponseBody(String accessToken) {
        String userInfoUri = googleOAuthHelper.getUserInfoUri();

        RestClient restClient = RestClient.create(userInfoUri);
        return restClient.get()
                .header(HttpHeaders.AUTHORIZATION, TokenType.BEARER.getType() + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, rep) -> {
                    throw new OAuthApiException(rep.getStatusCode(), rep.getHeaders());
                })
                .body(String.class);
    }

    @Override
    public User getAuthUser(String responseBody) {
        JsonElement element = JsonParser.parseString(responseBody);
        var email = element.getAsJsonObject().get("email").getAsString();
        var profileImg = element.getAsJsonObject().get("picture").getAsString();
        return createAuthUser(email, profileImg, GOOGLE);
    }

    private User createAuthUser(String email, String profileImg, Social social) {
        return User.builder()
                .email(email)
                .name(AuthNameGenerator.generateNameToEmail(email))
                .profileImg(profileImg)
                .role(Role.USER)
                .social(social)
                .password(CustomUUID.getCustomUUID(16, ""))
                .build();
    }
}
