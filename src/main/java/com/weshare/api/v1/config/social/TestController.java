package com.weshare.api.v1.config.social;

import com.weshare.api.v1.service.auth.login.provider.google.GoogleOAuthHelper;
import com.weshare.api.v1.service.auth.login.provider.kakao.KakaoOAuthHelper;
import com.weshare.api.v1.service.auth.login.provider.naver.NaverOAuthHelper;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class TestController {

    private final KakaoOAuthHelper kakaoOAuthHelper;
    private final NaverOAuthHelper naverOAuthHelper;
    private final GoogleOAuthHelper googleOAuthHelper;

    public TestController(KakaoOAuthHelper kakaoOAuthHelper, NaverOAuthHelper naverOAuthHelper, GoogleOAuthHelper googleOAuthHelper) {
        this.kakaoOAuthHelper = kakaoOAuthHelper;
        this.naverOAuthHelper = naverOAuthHelper;
        this.googleOAuthHelper = googleOAuthHelper;
    }

    @GetMapping("api/v1/auth/test")
    public List<String> getProvider() {
        List<String> kakao = kakaoOAuthHelper.getProvider();
        List<String> naver = naverOAuthHelper.getProvider();
        List<String> google = googleOAuthHelper.getProvider();
        System.out.println("kakao = " + kakao);
        System.out.println("naver = " + naver);
        System.out.println("google = " + google);
        return google;
    }
}
