package com.weShare.api.v1.auth;

import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationRequest {

  private String email;
  String password;
}
