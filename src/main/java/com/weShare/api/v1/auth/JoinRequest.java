package com.weShare.api.v1.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class JoinRequest {

  private String email;
  private String password;
  private LocalDate birthDate;

  @Builder
  private JoinRequest(String email, String password, LocalDate birthDate) {
    this.email = email;
    this.password = password;
    this.birthDate = birthDate;
  }
}
