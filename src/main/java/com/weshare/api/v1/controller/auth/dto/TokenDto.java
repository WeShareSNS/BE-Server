package com.weshare.api.v1.controller.auth.dto;


import java.util.Objects;

public record TokenDto(String accessToken, String refreshToken) {
    public TokenDto {
      Objects.requireNonNull(accessToken);
      Objects.requireNonNull(refreshToken);
    }
}
