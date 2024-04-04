package com.weshare.api.v1.controller.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

import java.time.LocalDate;

@Schema(description = "사용자 정보 수정 API")
public record UserUpdateRequest(
        @Schema(title = "사용자 프로필 변경", description = "사용자 프로필 정보는 URL 형태여야 합니다.")
        @URL(message = "url 정보가 올바르지 않습니다.")
        String profileImg,

        @Schema(title = "사용자 이름 변경", description = "사용자의 이름은 2~20자리 사이어야 합니다.")
        @Size(min = 2, max = 20, message = "닉네임은 2자리 이상 20자리 이하입니다.")
        String name,

        @Schema(title = "사용자 생년월일 변경", description = "사용자의 생년월일은 미래일 수 없습니다.")
        @Past(message = "과거의 시간만 요청이 가능합니다.")
        LocalDate birthDate
) {
}
