package com.weshare.api.v1.service.user.dto;

import io.jsonwebtoken.lang.Assert;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PasswordUpdateDto {
    private final String userEmail;
    private final String oldPassword;
    private final String newPassword;
    private final String verifyPassword;

    @Builder
    private PasswordUpdateDto(
            String userEmail,
            String oldPassword,
            String newPassword,
            String verifyPassword
    ) {
        Assert.hasText(userEmail, "사용자 이메일이 올바르지 않습니다.");
        this.userEmail = userEmail;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.verifyPassword = verifyPassword;
    }
}
