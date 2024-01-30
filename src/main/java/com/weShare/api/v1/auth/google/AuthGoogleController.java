package com.weShare.api.v1.auth.google;

import com.weShare.api.v1.auth.kakao.AuthCodeRequest;
import com.weShare.api.v1.auth.kakao.ResponseAuthToken;
import com.weShare.api.v1.auth.kakao.ResponseAuthUser;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthGoogleController {

    private final AuthGoogleService authGoogleService;

    @PostMapping("/google")
    @Operation(summary = "login", description = "카카오 계정을 통해서 로그인할 수 있으며 서버에 회원가입이 안되어 있으면 회원가입도 완료된다.")
    public ResponseEntity<ResponseAuthUser> loginWithKakaoAccount(@RequestBody AuthCodeRequest authCodeRequest) {
        ResponseAuthToken token = authGoogleService.getGoogleToken(authCodeRequest.code());
        log.info("Token={}", token);
        ResponseAuthUser authUser = authGoogleService.getGoogleUser(token.accessToken());
        log.info("authUser={}", authUser);

        return ResponseEntity.ok(authUser);
    }


    @GetMapping("/callback/google")
    public String test(@RequestParam String code){
        log.info(code);
        return code;
    }
}
