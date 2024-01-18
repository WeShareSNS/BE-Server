package com.weShare.api.v1.auth;

import lombok.*;

@Getter
@NoArgsConstructor
public class LoginRequest {

  private String email;
  private String password;

  @Builder
  private LoginRequest(String email, String password) {
    this.email = email;
    this.password = password;
  }
}
