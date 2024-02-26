package com.weshare.api.v1.service.auth.login.provider.kakao;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weshare.api.v1.common.CustomUUID;
import com.weshare.api.v1.domain.user.Role;
import com.weshare.api.v1.domain.user.Social;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.auth.login.OAuthApiException;
import com.weshare.api.v1.service.auth.login.provider.ExternalProvider;
import com.weshare.api.v1.service.auth.login.provider.ResponseAuthToken;
import com.weshare.api.v1.token.TokenType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import static com.weshare.api.v1.domain.user.Social.KAKAO;


@Component
public class KakaoLoginAndJoinProvider implements ExternalProvider {

    private final KakaoOAuthHelper kakaoOAuthHelper;

    public KakaoLoginAndJoinProvider(KakaoOAuthHelper kakaoOAuthHelper) {
        this.kakaoOAuthHelper = kakaoOAuthHelper;
    }

    @Override
    public boolean isIdentityProvider(String providerName) {
        return KAKAO.getProviderName().equals(providerName);
    }

    @Override
    public ResponseAuthToken getToken(String code) {
        String tokenUrl = kakaoOAuthHelper.getTokenUrl();
        var requestBody = kakaoOAuthHelper.getTokenRequestBody(code);
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
        String userInfoUri = kakaoOAuthHelper.getUserInfoUri();

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
        var profileImg = element.getAsJsonObject().get("properties").getAsJsonObject().get("profile_image").getAsString();
        var email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
        return createAuthUser(email, profileImg, KAKAO);
    }

    private User createAuthUser(String email, String profileImg, Social social) {
        return User.builder()
                .email(email)
                .name(CustomUUID.getCustomUUID(16, ""))
                .profileImg(profileImg)
                .role(Role.USER)
                .social(social)
                .password(CustomUUID.getCustomUUID(16, ""))
                .build();
    }
}
