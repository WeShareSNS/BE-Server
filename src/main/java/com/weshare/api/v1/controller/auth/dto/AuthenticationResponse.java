package com.weshare.api.v1.controller.auth.dto;

import java.util.Objects;

public record AuthenticationResponse (String accessToken) {
    public AuthenticationResponse {
        Objects.requireNonNull(accessToken);
    }
}
