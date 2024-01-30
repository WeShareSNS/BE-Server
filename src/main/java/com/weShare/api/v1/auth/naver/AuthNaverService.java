package com.weShare.api.v1.auth.naver;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weShare.api.v1.auth.kakao.OAuthApiException;
import com.weShare.api.v1.auth.kakao.ResponseAuthUser;
import com.weShare.api.v1.auth.kakao.ResponseAuthToken;
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
public class AuthNaverService {

    private final Environment evn;
    private final UserRepository userRepository;

    public ResponseAuthToken getNaverToken(String code) {
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

    public ResponseAuthUser getNaverUser(String accessToken) {
        log.info("token={}", accessToken);
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.naver.user-info-uri");

        RestClient restClient = RestClient.create(reqURL);
        String responseBody = restClient.post()
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

        return ResponseAuthUser.from(craeteAuthUser(responseBody));
    }

    private User craeteAuthUser(String responseBody) {
        JsonElement element = JsonParser.parseString(responseBody);
        String profileImg = element.getAsJsonObject().get("response").getAsJsonObject().get("profile_image").getAsString();
        String email = element.getAsJsonObject().get("response").getAsJsonObject().get("email").getAsString();
        String year = element.getAsJsonObject().get("response").getAsJsonObject().get("birthyear").getAsString();
        String date = element.getAsJsonObject().get("response").getAsJsonObject().get("birthday").getAsString();

        User user = User.builder()
                .email(email)
                .name(CustomUUID.getCustomUUID(16, ""))
                .profileImg(profileImg)
                .role(Role.USER)
                .password(CustomUUID.getCustomUUID(16, ""))
                .birthDate(LocalDate.parse(String.format("%s-%s", year, date)))
                .build();
        return userRepository.save(user);
    }
}
