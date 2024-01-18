package com.weShare.api.v1.auth;

import java.util.Objects;

public record AuthenticationResponse (String accessToken) {
    public AuthenticationResponse {
        Objects.requireNonNull(accessToken);
    }
}
