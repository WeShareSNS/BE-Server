package com.weShare.api.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Schema(description = "사용자 회원가입 요청 API")
@NoArgsConstructor
public class SignupRequest {

  @Schema(title = "사용자 이메일", description = "사용자 이메일을 입력해주세요")
  private String email;
  @Schema(title = "사용자 비밀번호", description = "사용자 비밀번호를 입력해주세요")
  private String password;
  @Schema(title = "사용자 생년월일", description = "사용자 생년월일을 yyyy-MM-dd 형식으로 입력해주세요")
  private LocalDate birthDate;

  @Builder
  private SignupRequest(String email, String password, LocalDate birthDate) {
    this.email = email;
    this.password = password;
    this.birthDate = birthDate;
  }
}
