package com.weShare.api.v1.auth.kakao;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

public class OAuthApiException extends ResponseStatusException {
    private final HttpHeaders headers;

    public OAuthApiException(HttpStatusCode status, HttpHeaders headers) {
        super(status, "API request failed");
        this.headers = headers;
    }

    public HttpHeaders getHeaders() {
        return headers;
    }
}
