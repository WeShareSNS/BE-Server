package com.weshare.api.v1.controller.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Schema(description = "사용자 로그인 요청 API")
@ToString
@NoArgsConstructor
public class LoginRequest {

  @Schema(title = "사용자 이메일", description = "사용자 이메일을 입력해주세요", nullable = true)
  @Email(message = "사용자 이메일 형식으로 작성해주세요")
  private String email;

  @Schema(title = "사용자 비밀번호", description = "사용자 비밀번호를 입력해주세요", nullable = true)
  @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
  private String password;

  @Schema(title = "OAuth2.0 code", description = "사용자의 인가 코드를 보내주세요", nullable = true)
  private String code;

  @Schema(title = "인가 서버이름", description = "code를 제공받은 인가서버 이름을 작성해주세요",
          allowableValues = {"google", "naver", "kakao"}, nullable = true)
  @Pattern(regexp = "^(google|naver|kakao)$", message = "인가서버 이름을 확인해주세요")
  private String identityProvider;

  @Builder
  private LoginRequest(String email, String password, String code, String identityProvider) {
    this.email = email;
    this.password = password;
    this.code = code;
    this.identityProvider = identityProvider;
  }
}
