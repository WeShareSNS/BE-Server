package com.weShare.api.v1.common;

import com.weShare.api.v1.token.TokenType;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;

@Component
public final class CookieTokenHandler {

    private static final String KEY = "Refresh-Token";
    private static final int EXPIRE_TIME = 7 * 24 * 60 * 60;

    public void setCookieToken(HttpServletResponse response, String refreshToken) {
        // create a cookie
        Cookie cookie = new Cookie(KEY, refreshToken);
        // expires in 7 days
        cookie.setMaxAge(EXPIRE_TIME);

        // optional properties
        cookie.setSecure(true);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

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
        validateBearerToken(authHeader);
        return authHeader.substring(7);
    }

    private void validateBearerToken(String token) {
        if (token == null || !token.startsWith(TokenType.BEARER.getType())) {
            throw new IllegalArgumentException("토큰 정보가 존재하지 않습니다.");
        }
    }
}
