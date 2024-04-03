package com.weshare.api.v1.service.user.dto;

import io.jsonwebtoken.lang.Assert;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.Optional;

@Getter
public class UserUpdateDto {
    String userEmail;
    Optional<String> profileImg;
    Optional<String> name;
    Optional<LocalDate> birthDate;

    @Builder
    private UserUpdateDto(
            String userEmail,
            String profileImg,
            String name,
            LocalDate birthDate
    ) {
        Assert.hasText(userEmail, "사용자 이메일이 올바르지 않습니다.");
        this.userEmail = userEmail;
        this.profileImg = Optional.ofNullable(profileImg);
        this.name = Optional.ofNullable(name);
        this.birthDate = Optional.ofNullable(birthDate);
    }
}
