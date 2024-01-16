package com.weShare.api.v1.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/join")
  public ResponseEntity<AuthenticationResponse> join(@RequestBody JoinRequest request) {
    return ResponseEntity.ok(service.join(request));
  }
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(service.login(request));
  }

  @PostMapping("/refresh-token")
  public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
    service.refreshToken(request, response);
  }

}
