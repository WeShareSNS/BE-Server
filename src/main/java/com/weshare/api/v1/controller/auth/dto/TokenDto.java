package com.weshare.api.v1.controller.auth.dto;


import jakarta.validation.constraints.NotNull;

public record TokenDto(@NotNull String accessToken,
                       @NotNull String refreshToken
) {
}
