package com.weshare.api.v1.service.auth.login.provider.naver;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weshare.api.v1.common.CustomUUID;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.auth.login.OAuthApiException;
import com.weshare.api.v1.service.auth.login.provider.AuthNameGenerator;
import com.weshare.api.v1.service.auth.login.provider.ExternalProvider;
import com.weshare.api.v1.service.auth.login.provider.ResponseAuthToken;
import com.weshare.api.v1.token.TokenType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

import static com.weshare.api.v1.domain.user.Social.NAVER;

@Component
public class NaverLoginAndJoinProvider implements ExternalProvider {

    private final NaverOAuthHelper naverOAuthHelper;

    public NaverLoginAndJoinProvider(NaverOAuthHelper naverOAuthHelper) {
        this.naverOAuthHelper = naverOAuthHelper;
    }

    @Override
    public boolean isIdentityProvider(String providerName) {
        return NAVER.getProviderName().equals(providerName);
    }

    @Override
    public ResponseAuthToken getToken(String code) {
        String tokenUrl = naverOAuthHelper.getTokenUrl();
        var requestBody = naverOAuthHelper.getTokenRequestBody(code);
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
        String userInfoUri = naverOAuthHelper.getUserInfoUri();
        RestClient restClient = RestClient.create(userInfoUri);

        return restClient.post()
                .headers(
                        httpHeaders -> {
                            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
                            httpHeaders.set(HttpHeaders.AUTHORIZATION, TokenType.BEARER.getType() + accessToken);
                        })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, rep) -> {
                    throw new OAuthApiException(rep.getStatusCode(), rep.getHeaders());
                })
                .toEntity(String.class)
                .getBody();
    }

    @Override
    public User getAuthUser(String responseBody) {
        JsonElement element = JsonParser.parseString(responseBody);
        var profileImg = element.getAsJsonObject().get("response").getAsJsonObject().get("profile_image").getAsString();
        var email = element.getAsJsonObject().get("response").getAsJsonObject().get("email").getAsString();
        var year = element.getAsJsonObject().get("response").getAsJsonObject().get("birthyear").getAsString();
        var date = element.getAsJsonObject().get("response").getAsJsonObject().get("birthday").getAsString();
        var birthDate = LocalDate.parse(String.format("%s-%s", year, date));
        return createAuthUser(email, profileImg, birthDate, NAVER);
    }

    private User createAuthUser(String email, String profileImg, LocalDate birthDate, Social social) {
        return User.builder()
                .email(email)
                .name(AuthNameGenerator.generateNameToEmail(email))
                .profileImg(profileImg)
                .role(Role.USER)
                .social(social)
                .password(CustomUUID.getCustomUUID(16, ""))
                .birthDate(birthDate)
                .build();
    }
}
