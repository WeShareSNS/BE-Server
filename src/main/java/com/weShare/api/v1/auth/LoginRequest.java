package com.weShare.api.v1.auth;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

  private String email;
  String password;
}
