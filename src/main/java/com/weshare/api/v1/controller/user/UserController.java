package com.weshare.api.v1.controller.user;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.controller.user.dto.UserUpdateRequest;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.user.UserService;
import com.weshare.api.v1.service.user.dto.UserUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me")
public class UserController {

    private final Response response;
    private final UserService userService;

    @PatchMapping
    public ResponseEntity updateUser(@Valid  @RequestBody(required = false) UserUpdateRequest userUpdateRequest,
                                     @AuthenticationPrincipal User user) {
        UserUpdateDto updateDto = createUpdateDto(userUpdateRequest, user);
        userService.updateUser(updateDto);
        return response.success();
    }

    private UserUpdateDto createUpdateDto(UserUpdateRequest userUpdateRequest, User user) {
        return UserUpdateDto.builder()
                .userEmail(user.getEmail())
                .profileImg(userUpdateRequest.profileImg())
                .birthDate(userUpdateRequest.birthDate())
                .name(userUpdateRequest.name())
                .build();
    }
}
