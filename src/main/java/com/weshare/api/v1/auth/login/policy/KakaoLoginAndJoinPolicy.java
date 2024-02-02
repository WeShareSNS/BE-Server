package com.weshare.api.v1.auth.login.policy;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weshare.api.v1.auth.login.OAuthApiException;
import com.weshare.api.v1.auth.login.ResponseAuthToken;
import com.weshare.api.v1.domain.user.entity.User;
import com.weshare.api.v1.domain.user.repository.UserRepository;
import com.weshare.api.v1.jwt.JwtService;
import com.weshare.api.v1.token.RefreshTokenRepository;
import com.weshare.api.v1.token.TokenType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import static com.weshare.api.v1.domain.Social.KAKAO;


@Component
public class KakaoLoginAndJoinPolicy extends AbstractProviderLoginAndJoinPolicy {

    @Value("${spring.security.oauth2.client.provider.kakao.token-uri}")
    private String tokenUrl;
    @Value("${spring.security.oauth2.client.registration.kakao.authorization-grant-type}")
    private String grantType;
    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;
    @Value("${spring.security.oauth2.client.registration.kakao.client-secret}")
    private String clientSecret;
    @Value("${spring.security.oauth2.client.registration.kakao.redirect-uri}")
    private String redirectUri;
    @Value("${spring.security.oauth2.client.provider.kakao.user-info-uri}")
    private String userInfoUri;

    public KakaoLoginAndJoinPolicy(
            UserRepository userRepository,
            RefreshTokenRepository refreshTokenRepository,
            JwtService jwtService
    ) {
        super(userRepository, refreshTokenRepository, jwtService);
    }

    @Override
    public boolean isIdentityProvider(String providerName) {
        return KAKAO.getProviderName().equals(providerName);
    }

    @Override
    protected ResponseAuthToken getToken(String code) {
        String reqURL = tokenUrl;
        var requestBody = getTokenRequestBody(code);
        RestClient restClient = RestClient.create(reqURL);

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

    private MultiValueMap<String, String> getTokenRequestBody(String code) {
        var body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("redirect_uri", redirectUri);
        body.add("code", code);
        return body;
    }

    @Override
    protected String getResponseBody(String accessToken) {
        String reqURL = userInfoUri;

        RestClient restClient = RestClient.create(reqURL);
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
    protected User getAuthUser(String responseBody) {
        JsonElement element = JsonParser.parseString(responseBody);
        var profileImg = element.getAsJsonObject().get("properties").getAsJsonObject().get("profile_image").getAsString();
        var email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
        return createAuthUser(email, profileImg, KAKAO);
    }
}
