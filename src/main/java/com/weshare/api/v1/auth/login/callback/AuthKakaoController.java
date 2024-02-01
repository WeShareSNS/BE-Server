package com.weshare.api.v1.auth.login.callback;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Tag(name = "OAuth", description = "카카오 API")
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/auth")
public class AuthKakaoController {

    @GetMapping("/callback/kakao")
    public String test(@RequestParam String code){
        log.info(code);
        return code;
    }

}
