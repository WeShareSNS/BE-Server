package com.weshare.api.v1.controller.auth.dto;


import org.springframework.util.Assert;

public record UserLoginDto(
        String accessToken,
        String refreshToken,
        String name
) {
    public UserLoginDto {
        Assert.hasText(accessToken, "access 토큰 정보가 존재하지 않습니다.");
        Assert.hasText(accessToken, "refresh 토큰 정보가 존재하지 않습니다.");
        Assert.hasText(name, "사용자 닉네임이 존재하지 않습니다.");
    }
}
