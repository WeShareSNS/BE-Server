package com.weShare.api.v1.auth.controller.dto;

import java.util.Objects;

public record AuthenticationResponse (String accessToken) {
    public AuthenticationResponse {
        Objects.requireNonNull(accessToken);
    }
}
