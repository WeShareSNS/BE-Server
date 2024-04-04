package com.weshare.api.v1.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Schema(description = "사용자 비밀번호 수정 API")
public record PasswordUpdateRequest(
        @Schema(title = "사용자 기존 비밀번호", description = "사용자의 기존 비밀번호를 입력해주세요.")
        @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
        @NotBlank
        String oldPassword,
        @Schema(title = "사용자가 변경할 비밀번호", description = "사용자가 변경할 비밀번호를 입력해주세요.")
        @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
        @NotBlank
        String newPassword,
        @Schema(title = "사용자가 변경할 비밀번호 재입력", description = "사용자가 변경할 비밀번호의 재입력 값을 입력해주세요.")
        @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
        @NotBlank
        String verifyPassword
) {
}
