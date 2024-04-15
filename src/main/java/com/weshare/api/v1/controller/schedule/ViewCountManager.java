package com.weshare.api.v1.controller.schedule;

import com.weshare.api.v1.service.schedule.command.ScheduleService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public final class ViewCountManager {
    private static final String KEY = "View-Count";
    private static final int EXPIRE_TIME = 24 * 60 * 60;
    public static final String CookieFormat = "%s:%s";

    private final ScheduleService scheduleService;

    public void viewCountUp(Long id, HttpServletRequest request, HttpServletResponse response) {
        Optional<Cookie> cookie = Arrays.stream(request.getCookies())
                .filter(c -> KEY.equals(c.getName()))
                .findAny();

        if (cookie.isPresent()) {
            Cookie oldCookie = cookie.get();
            updateCookieAndViewCount(id, response, oldCookie);
            return;
        }
        scheduleService.viewCount(id);
        setResponseCookie(id, response, Optional.empty());
    }

    private void updateCookieAndViewCount(Long id, HttpServletResponse response, Cookie oldCookie) {
        String value = oldCookie.getValue();
        // 중복제거 + contains 사용을 위해서 set 사용
        final Set<String> scheduleIds = Arrays.stream(value.split(":"))
                .collect(Collectors.toUnmodifiableSet());

        if (!scheduleIds.contains(String.valueOf(id))) {
            scheduleService.viewCount(id);
            setResponseCookie(id, response, Optional.of(value));
        }
    }

    private void setResponseCookie(Long id, HttpServletResponse response, Optional<String> value) {
        String cookieValue = String.valueOf(id);
        if (value.isPresent()) {
            cookieValue = String.format(CookieFormat, value.get(), cookieValue);
        }
        response.addCookie(createCookie(cookieValue));
    }

    private Cookie createCookie(String value) {
        final Cookie newCookie = new Cookie(KEY, value);
        newCookie.setMaxAge(EXPIRE_TIME);
        newCookie.setPath("/");
        return newCookie;
    }
}
