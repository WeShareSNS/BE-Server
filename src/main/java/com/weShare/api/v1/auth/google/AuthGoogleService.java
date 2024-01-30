package com.weShare.api.v1.auth.google;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weShare.api.v1.auth.kakao.OAuthApiException;
import com.weShare.api.v1.auth.kakao.ResponseAuthToken;
import com.weShare.api.v1.auth.kakao.ResponseAuthUser;
import com.weShare.api.v1.common.CustomUUID;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import com.weShare.api.v1.token.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;

@Service
@Slf4j
@Transactional
@Component
@RequiredArgsConstructor
public class AuthGoogleService {

    private final Environment evn;
    private final UserRepository userRepository;

    public ResponseAuthToken getGoogleToken(String code) {
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.google.token-uri");
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
        body.add("grant_type", evn.getProperty("spring.security.oauth2.client.registration.google.authorization-grant-type"));
        body.add("client_id", evn.getProperty("spring.security.oauth2.client.registration.google.client-id"));
        body.add("client_secret", evn.getProperty("spring.security.oauth2.client.registration.google.client-secret"));
        body.add("redirect_uri", evn.getProperty("spring.security.oauth2.client.registration.google.redirect-uri"));
        body.add("code", code);
        return body;
    }

    public ResponseAuthUser getGoogleUser(String accessToken) {
        log.info("token={}", accessToken);
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.google.user-info-uri");

        RestClient restClient = RestClient.create(reqURL);
        String responseBody = restClient.get()
                .header(HttpHeaders.AUTHORIZATION, TokenType.BEARER.getType() + accessToken)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, rep) -> {
                    throw new OAuthApiException(rep.getStatusCode(), rep.getHeaders());
                })
                .body(String.class);

        return ResponseAuthUser.from(craeteAuthUser(responseBody));
    }

    private User craeteAuthUser(String responseBody) {
        JsonElement element = JsonParser.parseString(responseBody);
        log.info("reall result={}",element);
        String email = element.getAsJsonObject().get("email").getAsString();
        String profileImg = element.getAsJsonObject().get("picture").getAsString();

        User user = User.builder()
                .email(email)
                .name(CustomUUID.getCustomUUID(16, ""))
                .profileImg(profileImg)
                .role(Role.USER)
                .password(CustomUUID.getCustomUUID(16, ""))
                .build();
        return userRepository.save(user);
    }
}
