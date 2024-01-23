package com.weShare.api.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Schema(description = "사용자 로그인 요청 API")
@NoArgsConstructor
public class LoginRequest {

  @Schema(title = "사용자 이메일", description = "사용자 이메일을 입력해주세요")
  @Email(message = "사용자 이메일 형식으로 작성해주세요")
  private String email;
  @Schema(title = "사용자 비밀번호", description = "사용자 비밀번호를 입력해주세요")
  @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
  private String password;

  @Builder
  private LoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
