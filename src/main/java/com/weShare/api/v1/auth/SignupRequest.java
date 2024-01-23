package com.weShare.api.v1.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@Schema(description = "사용자 회원가입 요청 API")
@NoArgsConstructor
public class SignupRequest {

  @Schema(title = "사용자 이메일", description = "사용자 이메일을 입력해주세요")
  @Email(message = "사용자 이메일 형식으로 작성해주세요")
  private String email;
  @Schema(title = "사용자 비밀번호", description = "사용자 비밀번호를 입력해주세요")
  @Size(min = 8, max = 16, message = "비밀번호는 8자리 이상 16자리 이하입니다.")
  private String password;
  @Schema(title = "사용자 생년월일", description = "사용자 생년월일을 yyyy-MM-dd 형식으로 입력해주세요")
  @Pattern(regexp = "^([12]\\d{3}-(0[1-9]|1[0-2])-(0[1-9]|[12]\\d|3[01]))$",
           message = "생년월일을 1970-01-01 형식으로 입력해주세요")
  private String birthDate;


  @Builder
  private SignupRequest(String email, String password, String birthDate) {
    this.email = email;
    this.password = password;
    this.birthDate = birthDate;
  }
}
