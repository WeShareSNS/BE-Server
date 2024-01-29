package com.weShare.api.v1.auth.kakao;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.weShare.api.v1.common.CustomUUID;
import com.weShare.api.v1.domain.user.Role;
import com.weShare.api.v1.domain.user.entity.User;
import com.weShare.api.v1.domain.user.repository.UserRepository;
import com.weShare.api.v1.token.TokenType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

@Service
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthKaKaoService {

    private final Environment evn;
    private final UserRepository userRepository;

    public ResponseKaKaoToken getKakaoToken(String code) {
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.token-uri");
        MultiValueMap<String, String> body = getTokenRequestParam(code);
        RestClient restClient = RestClient.create(reqURL);

        return restClient.post()
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(body)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                    throw new AuthKakaoApiException(response.getStatusCode(), response.getHeaders());
                })
                .toEntity(ResponseKaKaoToken.class)
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

    public ResponseAuthUser getKakaoUser(String accessToken) {
        String reqURL = evn.getProperty("spring.security.oauth2.client.provider.kakao.user-info-uri");

        RestClient restClient = RestClient.create(reqURL);
        String responseBody = restClient.post()
                .headers(
                        httpHeaders -> {
                            httpHeaders.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE + ";charset=UTF-8");
                            httpHeaders.set(HttpHeaders.AUTHORIZATION, TokenType.BEARER.getType() + accessToken);
                        })
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, (req, rep) -> {
                    throw new AuthKakaoApiException(rep.getStatusCode(), rep.getHeaders());
                })
                .toEntity(String.class)
                .getBody();

        return ResponseAuthUser.from(craeteAuthUser(responseBody));
    }

    private User craeteAuthUser(String responseBody) {
        JsonElement element = JsonParser.parseString(responseBody);
        String profileImg = element.getAsJsonObject().get("properties").getAsJsonObject().get("thumbnail_image").getAsString();
        String email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();

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