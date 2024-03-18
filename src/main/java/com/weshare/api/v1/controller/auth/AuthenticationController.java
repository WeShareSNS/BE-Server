package com.weshare.api.v1.controller.auth;

import com.weshare.api.v1.controller.auth.dto.*;
import com.weshare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weshare.api.v1.service.auth.AuthenticationService;
import com.weshare.api.v1.common.CookieTokenHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Optional;

@Tag(name = "auth-controller", description = "사용자 인증을 위한 컨트롤러")
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

  private final EmailValidator validator;
  private final AuthenticationService service;
  private final CookieTokenHandler cookieTokenHandler;

  @Operation(summary = "사용자 회원가입 API", description = "사용자는 회원가입을 할 수 있습니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200",
                  description = "로그인이 성공했습니다. 사용자의 이름은 UUID, 프로필은 기본 프로필 이미지 URL이 등록됩니다."),
          @ApiResponse(responseCode = "400", description = "http body를 확인해주세요"),
          @ApiResponse(responseCode = "409", description = "사용자 이메일이 중복되었습니다.")
  })
  @PostMapping("/signup")
  public ResponseEntity join(@Valid @RequestBody SignupRequest request) {

    service.join(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @Operation(summary = "사용자 이메일 중복 확인 API", description = "사용자는 이메일을 중복확인 할 수 있습니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "사용 가능한 이메일 입니다."),
          @ApiResponse(responseCode = "400", description = "이메일 양식을 확인해주세요"),
          @ApiResponse(responseCode = "409", description = "사용자 이메일이 중복되었습니다.")
  })
  @GetMapping("/signup/duplicate-email")
  public ResponseEntity duplicateEmail(@RequestParam String email) {
    validator.validateEmailFormat(email);
    service.checkDuplicateEmailForSignup(email);
    return ResponseEntity.status(HttpStatus.OK).build();
  }

  @Operation(summary = "사용자 로그인 API", description = "사용자는 로그인을 할 수 있습니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "refresh 토큰은 cookie에 저장됩니다."),
          @ApiResponse(responseCode = "201", description = "간편 로그인 사용자가 신규 유저인 경우 회원가입 처리합니다."),
          @ApiResponse(responseCode = "400", description = "http body를 확인해주세요"),
          @ApiResponse(responseCode = "404", description = "이메일을 확인해 주세요")
  })
  @PostMapping("/signin")
  public ResponseEntity<AuthenticationResponse> login(@Valid @RequestBody(required = false) LoginRequest request,
                                                      HttpServletResponse response) {
    log.info("data={}", request);
    Optional<TokenDto> tokenDtoOptional = service.login(request, new Date(System.nanoTime()));
    if (tokenDtoOptional.isEmpty()) {
      return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    TokenDto tokenDto = tokenDtoOptional.get();
    cookieTokenHandler.setCookieToken(response, tokenDto.refreshToken());
    return ResponseEntity.ok(new AuthenticationResponse(tokenDto.accessToken()));
  }

  @Operation(summary = "사용자 access 토큰 재발급 API",
          description = "사용자는 쿠키에 저장되어있는 Refresh 토큰을 통해서 access 토큰 재발급 할 수 있습니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200", description = "토큰 재발급 성공"),
          @ApiResponse(responseCode = "401", description = "쿠키가 만료되어 있는지 확인해주세요"),
  })
  @GetMapping("/reissue-token")
  public ResponseEntity<AuthenticationResponse> reissueToken(@CookieValue("Refresh-Token") Optional<String> refreshToken,
                                                             HttpServletResponse response) {
    TokenDto tokenDto = service.reissueToken(refreshToken, new Date(System.nanoTime()));
    cookieTokenHandler.setCookieToken(response, tokenDto.refreshToken());
    return ResponseEntity.ok(new AuthenticationResponse(tokenDto.accessToken()));
  }

  @Operation(security = { @SecurityRequirement(name = "bearer-key") },
          summary = "사용자 로그아웃 API", description = "사용자는 로그아웃을 할 수 있습니다.")
  @ApiResponses({
          @ApiResponse(responseCode = "200",
                  description = "로그아웃 성공 토큰에 있는 refresh 토큰을 제거합니다."),
  })
  @PostMapping("/logout")
  public ResponseEntity logout(HttpServletRequest request, HttpServletResponse response) {
    String accessToken = cookieTokenHandler.getBearerToken(request);
    service.logout(accessToken);
    cookieTokenHandler.expireCookieToken(response);
    return ResponseEntity.ok().build();
  }
}
