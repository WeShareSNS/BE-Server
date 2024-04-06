package com.weshare.api.v1.controller.user.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteUserRequest(@NotBlank String password) {
}
