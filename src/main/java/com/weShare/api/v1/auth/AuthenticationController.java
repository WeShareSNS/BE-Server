package com.weShare.api.v1.auth;

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
  public ResponseEntity signup(@RequestBody SignupRequest request) {
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
  public ResponseEntity<AuthenticationResponse> reissueToken(@RequestBody ReissueTokenRequest tokenRequest,
                                                                          HttpServletResponse response) {
    TokenDto tokenDto = service.reissueToken(tokenRequest.getRefreshToken());
    cookieTokenHandler.setCookieToken(response, tokenDto.refreshToken());
    return ResponseEntity.ok(new AuthenticationResponse(tokenDto.accessToken()));
  }

  @PostMapping("/logout")
  public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = cookieTokenHandler.getBearerToken(request);
    service.logout(accessToken);
    cookieTokenHandler.expireCookieToken(response);
    return ResponseEntity.ok().build();
  }

}
