package com.weshare.api.v1.controller.auth.dto;

import jakarta.validation.constraints.NotNull;

public record AuthenticationResponse (@NotNull String accessToken) {
}
