package com.weShare.api.v1.auth.login;


import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weShare.api.v1.auth.controller.dto.LoginRequest;
import com.weShare.api.v1.auth.controller.dto.TokenDto;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import com.weShare.api.v1.jwt.JwtService;
import com.weShare.api.v1.token.RefreshTokenRepository;
import com.weShare.api.v1.token.TokenType;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.util.Date;

public class NaverLoginAndJoinPolicy extends AbstractProviderLoginAndJoinPolicy {

    private static final String PROVIDER_NAME = "naver";

    public NaverLoginAndJoinPolicy(Environment evn, UserRepository userRepository, RefreshTokenRepository refreshTokenRepository, JwtService jwtService) {
        super(evn, userRepository, refreshTokenRepository, jwtService);
    }

    @Override
    public TokenDto login(LoginRequest request, Date issuedAt) {
        ResponseAuthToken token = getToken(request.getCode());
        String responseBody = getResponseBody(token.accessToken());
        User authUser = getAuthUser(responseBody);
        TokenDto tokenDto = getTokenDto(authUser, issuedAt);
        reissueRefreshTokenByUser(authUser, tokenDto.refreshToken());
        return tokenDto;
    }

    @Override
    public boolean isIdentityProvider(String providerName) {
        return PROVIDER_NAME.equals(providerName);
    }

    private ResponseAuthToken getToken(String code) {
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.naver.token-uri");
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
        MultiValueMap<String, String> body = new LinkedMultiValueMap();
        body.add("grant_type", evn.getProperty("spring.security.oauth2.client.registration.naver.authorization-grant-type"));
        body.add("client_id", evn.getProperty("spring.security.oauth2.client.registration.naver.client-id"));
        body.add("client_secret", evn.getProperty("spring.security.oauth2.client.registration.naver.client-secret"));
        body.add("redirect_uri", evn.getProperty("spring.security.oauth2.client.registration.naver.redirect-uri"));
        body.add("state", evn.getProperty("spring.security.oauth2.client.registration.naver.state"));
        body.add("code", code);
        return body;
    }

    private String getResponseBody(String accessToken) {
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.naver.user-info-uri");

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

    private User getAuthUser(String responseBody) {
        JsonElement element = JsonParser.parseString(responseBody);
        String profileImg = element.getAsJsonObject().get("response").getAsJsonObject().get("profile_image").getAsString();
        String email = element.getAsJsonObject().get("response").getAsJsonObject().get("email").getAsString();
        String year = element.getAsJsonObject().get("response").getAsJsonObject().get("birthyear").getAsString();
        String date = element.getAsJsonObject().get("response").getAsJsonObject().get("birthday").getAsString();
        LocalDate birthDate = LocalDate.parse(String.format("%s-%s", year, date));
        return createAuthUser(email, profileImg, birthDate);
    }
}
