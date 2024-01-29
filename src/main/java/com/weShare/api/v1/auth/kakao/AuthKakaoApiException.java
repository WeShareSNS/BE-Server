package com.weShare.api.v1.auth.kakao;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class AuthKakaoApiException extends ResponseStatusException {
    private final HttpHeaders headers;

    public AuthKakaoApiException(HttpStatusCode status, HttpHeaders headers) {
        super(status, "Kakao API request failed");
        this.headers = headers;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}
