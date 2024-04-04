package com.weshare.api.v1.controller.auth.dto;

public record AuthenticationResponse (
        String username,
        String accessToken
) {
}
