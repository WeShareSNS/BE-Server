package com.weShare.api.v1.auth.naver;

import com.weShare.api.v1.auth.kakao.AuthCodeRequest;
import com.weShare.api.v1.auth.kakao.ResponseAuthUser;
import com.weShare.api.v1.auth.kakao.ResponseAuthToken;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthNaverController {

    private final AuthNaverService authNaverService;

    @PostMapping("/naver")
    @Operation(summary = "login", description = "카카오 계정을 통해서 로그인할 수 있으며 서버에 회원가입이 안되어 있으면 회원가입도 완료된다.")
    public ResponseEntity<ResponseAuthUser> loginWithKakaoAccount(@RequestBody AuthCodeRequest authCodeRequest) {
        ResponseAuthToken token = authNaverService.getNaverToken(authCodeRequest.code());
        log.info("Token={}", token);
        ResponseAuthUser authUser = authNaverService.getNaverUser(token.accessToken());
        log.info("authUser={}", authUser);

        return ResponseEntity.ok(authUser);
    }

    @GetMapping("/callback/naver")
    public String test(@RequestParam String code){
        log.info(code);
        return code;
    }
}
