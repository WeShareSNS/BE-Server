package com.weshare.api.v1.auth.login;

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
