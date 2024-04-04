package com.weshare.api.v1.controller.user.dto;

import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

public record UserUpdateRequest(
        @URL(message = "url 정보가 올바르지 않습니다.")
        String profileImg,
        @Size(min = 2, max = 20, message = "닉네임은 2자리 이상 20자리 이하입니다.")
        String name,
        @Past(message = "과거의 시간만 요청이 가능합니다.")
        LocalDate birthDate
) {
}
