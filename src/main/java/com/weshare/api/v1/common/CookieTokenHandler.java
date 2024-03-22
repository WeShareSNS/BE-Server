package com.weshare.api.v1.common;

import com.weshare.api.v1.token.TokenType;
import com.weshare.api.v1.token.exception.TokenNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public final class CookieTokenHandler {

    private static final String KEY = "Refresh-Token";
    private static final int EXPIRE_TIME = 7 * 24 * 60 * 60;
    private static final int BEARER_HEADER_LENGTH = 7;

    public void setCookieToken(HttpServletResponse response, String refreshToken) {
        log.info("cookieHandler={}",refreshToken);
        // create a cookie
        Cookie cookie = new Cookie(KEY, refreshToken);
        // expires in 7 days
        cookie.setMaxAge(EXPIRE_TIME);

        // optional properties
//        cookie.setSecure(true); localhost test를 위해서 잠시 주석
        cookie.setHttpOnly(true);
        cookie.setPath("/");
//        cookie.setDomain(DOMAIN);

        // add cookie to response
        response.addCookie(cookie);
    }

    public void expireCookieToken(HttpServletResponse response) {
        Cookie cookie = new Cookie(KEY, null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }

    public String getBearerToken(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (hasInvalidBearerTokenFormat(authHeader)) {
            throw new TokenNotFoundException("토큰 정보가 존재하지 않습니다.");
        }
        return authHeader.substring(BEARER_HEADER_LENGTH);
    }

    private boolean hasInvalidBearerTokenFormat(String token) {
        return token == null || !token.startsWith(TokenType.BEARER.getType());
    }
}
