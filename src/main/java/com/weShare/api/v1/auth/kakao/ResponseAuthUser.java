package com.weShare.api.v1.auth.kakao;

import com.weShare.api.v1.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ResponseAuthUser {

    private String name;
    private String email;
    private String profileImg;
    private LocalDate birthDate;

    @Builder
    private ResponseAuthUser(String name, String email, String profileImg, LocalDate birthDate) {
        this.name = name;
        this.email = email;
        this.profileImg = profileImg;
        this.birthDate = birthDate;
    }

    public static ResponseAuthUser from(User user) {
        return ResponseAuthUser.builder()
                .email(user.getEmail())
                .name(user.getName())
                .profileImg(user.getProfileImg())
                .birthDate(user.getBirthDate())
                .build();
    }

}
