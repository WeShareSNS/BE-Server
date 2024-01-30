package com.weShare.api.v1.auth.login.policy;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weShare.api.v1.auth.login.OAuthApiException;
import com.weShare.api.v1.auth.login.ResponseAuthToken;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import com.weShare.api.v1.jwt.JwtService;
import com.weShare.api.v1.token.RefreshTokenRepository;
import com.weShare.api.v1.token.TokenType;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;


@Log4j2
public class KakaoLoginAndJoinPolicy extends AbstractProviderLoginAndJoinPolicy {

    private static final String PROVIDER_NAME = "kakao";

    public KakaoLoginAndJoinPolicy(Environment evn, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        super(evn, userRepository, refreshTokenRepository, jwtService);
    }

    @Override
    public boolean isIdentityProvider(String providerName) {
        return PROVIDER_NAME.equals(providerName);
    }

    @Override
    protected ResponseAuthToken getToken(String code) {
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.token-uri");
        MultiValueMap<String, String> body = getTokenRequestParam(code);
        RestClient restClient = RestClient.create(reqURL);

        return restClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new OAuthApiException(response.getStatusCode(), response.getHeaders());
                })
                .toEntity(ResponseAuthToken.class)
                .getBody();
    }

    private MultiValueMap<String, String> getTokenRequestParam(String code) {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
        body.add("grant_type", evn.getProperty("spring.security.oauth2.client.registration.kakao.authorization-grant-type"));
        body.add("client_id", evn.getProperty("spring.security.oauth2.client.registration.kakao.client-id"));
        body.add("client_secret", evn.getProperty("spring.security.oauth2.client.registration.kakao.client-secret"));
        body.add("redirect_uri", evn.getProperty("spring.security.oauth2.client.registration.kakao.redirect-uri"));
        body.add("code", code);
        return body;
    }

    @Override
    protected String getResponseBody(String accessToken) {
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.user-info-uri");

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
        log.info("result={}", responseBody);
        JsonElement element = JsonParser.parseString(responseBody);
        String profileImg = element.getAsJsonObject().get("properties").getAsJsonObject().get("profile_image").getAsString();
        String email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
        return createAuthUser(email, profileImg);
    }
}
