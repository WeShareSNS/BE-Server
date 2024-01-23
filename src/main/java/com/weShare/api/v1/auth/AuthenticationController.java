package com.weShare.api.v1.auth;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "auth-controller", description = "사용자 인증을 위한 컨트롤러")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

  private final AuthenticationService service;
  private final CookieTokenHandler cookieTokenHandler;

  @Operation(summary = "사용자 회원가입 API", description = "사용자는 회원가입을 할 수 있습니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200",
                  description = " 로그인 성공: 사용자의 이름은 UUID, 프로필은 기본 프로필 이미지 URL이 등록됩니다."),
          @ApiResponse(responseCode = "400", description = "입력 파라미터를 확인해주세요"),
          @ApiResponse(responseCode = "409", description = "사용자 이메일이 중복되었습니다.")
  })
  @PostMapping("/signup")
  public ResponseEntity signup(@Valid @RequestBody SignupRequest request) {
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
