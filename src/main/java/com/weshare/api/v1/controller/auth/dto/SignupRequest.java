package com.weshare.api.v1.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "사용자 회원가입 요청 API")
public record SignupRequest(
        @Schema(title = "사용자 이메일", description = "사용자 이메일을 입력해주세요")
        @Email(message = "사용자 이메일 형식으로 작성해주세요")
        @NotBlank(message = "이메일은 필수 입니다.")
        String email,
        @Schema(title = "사용자 닉네임", description = "사용자 닉네임을 입력해주세요")
        @Size(min = 2, max = 20, message = "닉네임은 2~20 글자 사이어야 합니다.")
        @NotBlank(message = "이메일은 필수 입니다.")
        String userName,
        @Schema(title = "사용자 비밀번호", description = "사용자 비밀번호를 입력해주세요")
        @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
        @NotBlank(message = "비밀번호는 필수 입니다.")
        String password,
        @Schema(title = "사용자 생년월일", description = "사용자 생년월일을 yyyy-MM-dd 형식으로 입력해주세요")
        @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$",
                message = "생년월일을 1970-01-01 형식으로 입력해주세요")
        @NotBlank(message = "생년월일은 필수 입니다.")
        String birthDate
) {
}
