package com.weShare.api.v1.auth;

import com.weShare.api.v1.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.service.annotation.HttpExchange;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;

  @PostMapping("/signup")
  public ResponseEntity<User> signup(@RequestBody JoinRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
            .body(service.join(request));
  }
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request) {
    return ResponseEntity.ok(service.login(request));
  }

  @PostMapping("/reissue-token")
  public ResponseEntity<AuthenticationResponse> refreshToken(HttpServletRequest request) {
    return ResponseEntity.ok(service.refreshToken(request));
  }

  @PostMapping("/logout")
  public ResponseEntity logout(HttpServletRequest request) {
    service.logout(request);
    return ResponseEntity.ok().build();
  }

}
