package com.weShare.api.v1.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class JoinRequest {

  private String email;
  private String password;
  private LocalDate birthDate;

}
