package com.weShare.api.v1.auth.login.callback;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthGoogleController {

    @GetMapping("/callback/google")
    public String test(@RequestParam String code){
        log.info(code);
        return code;
    }
}
