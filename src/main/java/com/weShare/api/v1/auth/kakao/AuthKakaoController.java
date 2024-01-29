package com.weShare.api.v1.auth.kakao;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "OAuth", description = "카카오 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthKakaoController {

    private final AuthKaKaoService oAuthKakaoService;

    @PostMapping("/login")
    @Operation(summary = "login", description = "카카오 계정을 통해서 로그인할 수 있으며 서버에 회원가입이 안되어 있으면 회원가입도 완료된다.")
    public ResponseEntity<ResponseAuthUser> loginWithKakaoAccount(@RequestBody AuthCodeRequest authCodeRequest) {
        ResponseKaKaoToken token = oAuthKakaoService.getKakaoToken(authCodeRequest.code());
        ResponseAuthUser kakaoUser = oAuthKakaoService.getKakaoUser(token.accessToken());
        log.info("kakaoToken={}", token);
        log.info("kakaoUser={}", kakaoUser);

        return ResponseEntity.ok(kakaoUser);
    }

    @GetMapping("/callback/kakao")
    public String test(@RequestParam String code){
        log.info(code);
        return code;
    }

}
