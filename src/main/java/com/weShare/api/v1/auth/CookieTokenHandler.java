package com.weShare.api.v1.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

public final class CookieTokenHandler {

    private static final String KEY = "Refresh-Token";
    private static final int EXPIRE_TIME = 7 * 24 * 60 * 60;

    public void setCookieToken(HttpServletResponse response, String refreshToken) {
        // create a cookie
        Cookie cookie = new Cookie(KEY,refreshToken);
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

    public static String getKey() {
        return KEY;
    }
}
