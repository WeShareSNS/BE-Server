package com.weshare.api.v1.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
@Schema(description = "사용자 로그인 요청 API")
public record LoginRequest(
        @Schema(title = "사용자 이메일", description = "사용자 이메일을 입력해주세요", nullable = true)
        @Email(message = "사용자 이메일 형식으로 작성해주세요")
        String email,

        @Schema(title = "사용자 비밀번호", description = "사용자 비밀번호를 입력해주세요", nullable = true)
        @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
        String password,

        @Schema(title = "OAuth2.0 code", description = "사용자의 인가 코드를 보내주세요", nullable = true)
        String code,

        @Schema(title = "인가 서버이름", description = "code를 제공받은 인가서버 이름을 작성해주세요",
                allowableValues = {"google", "naver", "kakao"}, nullable = true)
        @Pattern(regexp = "^(google|naver|kakao)$", message = "인가서버 이름을 확인해주세요")
        String identityProvider
) {
}
