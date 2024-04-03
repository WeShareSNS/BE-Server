package com.weshare.api.v1.controller.user.dto;

import jakarta.validation.constraints.Past;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

public record UserUpdateRequest(
        @URL(message = "url 정보가 올바르지 않습니다.")
        String profileImg,
        String name,
        @Past(message = "과거의 시간만 요청이 가능합니다.")
        LocalDate birthDate
) {
}
