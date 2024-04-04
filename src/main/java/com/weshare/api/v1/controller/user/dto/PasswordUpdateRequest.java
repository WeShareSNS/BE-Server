package com.weshare.api.v1.controller.user.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PasswordUpdateRequest(
        @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
        @NotBlank
        String oldPassword,
        @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
        @NotBlank
        String newPassword,
        @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
        @NotBlank
        String verifyPassword
) {
}
