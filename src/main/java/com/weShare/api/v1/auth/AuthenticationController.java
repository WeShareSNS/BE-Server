package com.weShare.api.v1.auth;

import com.weShare.api.v1.common.Response;
import com.weShare.api.v1.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final CookieTokenHandler cookieTokenHandler;

  @PostMapping("/signup")
  public ResponseEntity<User> signup(@RequestBody SignupRequest request) {
    service.signup(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
  @PostMapping("/login")
  public ResponseEntity<AuthenticationResponse> login(@RequestBody LoginRequest request, HttpServletResponse response) {
    TokenDto tokenDto = service.login(request);
    cookieTokenHandler.setCookieToken(response, tokenDto.refreshToken());
    return ResponseEntity.ok(new AuthenticationResponse(tokenDto.accessToken()));
  }

  @PostMapping("/reissue-token")
  public ResponseEntity<AuthenticationResponse> reissueToken(HttpServletRequest request,
                                                             HttpServletResponse response) {
    TokenDto tokenDto = service.reissueToken(request);
    cookieTokenHandler.setCookieToken(response, tokenDto.refreshToken());
    return ResponseEntity.ok(new AuthenticationResponse(tokenDto.accessToken()));
  }

  @PostMapping("/logout")
  public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
    service.logout(request);
    cookieTokenHandler.expireCookieToken(response);
    return ResponseEntity.ok().build();
  }

}
