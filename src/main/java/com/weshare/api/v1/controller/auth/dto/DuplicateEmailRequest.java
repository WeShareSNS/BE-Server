package com.weshare.api.v1.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Schema(description = "사용자 로그인 요청 API")
public record DuplicateEmailRequest(
        @Schema(title = "사용자 이메일", description = "사용자 이메일을 입력해주세요")
        @NotNull(message = "이메일을 입력해주세요")
        @Email(message = "사용자 이메일 형식으로 작성해주세요")
        @Getter
        String email
) {
}
