package com.weshare.api.v1.controller.auth;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class UserSignupValidator {
    private static final Pattern EMAIL_REGEX = Pattern.compile("^[a-zA-Z0-9+-\\_.]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$");

    public void validateEmailFormat(String email) {
        if (email.isBlank()) {
            throw new IllegalArgumentException("이메일을 입력해주세요");
        }
        if (!isValidEmail(email)) {
            throw new IllegalArgumentException("올바른 이메일 형식이 아닙니다.");
        }
    }

    private boolean isValidEmail(String email) {
        return EMAIL_REGEX.matcher(email).matches();
    }

    public void validateNameLength(String name) {
        int length = name.length();
        if (2 > length || length > 12) {
            throw new IllegalArgumentException("닉네임은 2~12 글자 사이어야 합니다.");
        }
    }
}
