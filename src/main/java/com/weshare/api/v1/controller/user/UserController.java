package com.weshare.api.v1.controller.user;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.controller.user.dto.PasswordUpdateRequest;
import com.weshare.api.v1.controller.user.dto.UserUpdateRequest;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.schedule.query.dto.UserScheduleDto;
import com.weshare.api.v1.service.user.UserService;
import com.weshare.api.v1.service.user.dto.PasswordUpdateDto;
import com.weshare.api.v1.service.user.dto.UserDeleteDto;
import com.weshare.api.v1.service.user.dto.UserUpdateDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me")
public class UserController {

    private final Response response;
    private final UserService userService;

    @PatchMapping
    public ResponseEntity updateUser(@Valid  @RequestBody(required = false) UserUpdateRequest userUpdateRequest,
                                     @AuthenticationPrincipal User user) {
        UserUpdateDto updateDto = createUserUpdateDto(userUpdateRequest, user);
        userService.updateUser(updateDto);
        return response.success();
    }
    // 비밀번호 어떻게 할건지 물어보기 따로 분리할건지 -> 프론트에서 검증하고 보낸다는게 믿을 수 없늬까 한번에 기존 비밀번호랑 새로운 비밀번호를 받는게 좋아보이는데 잘 모르겠음
    @PatchMapping("/password")
    public ResponseEntity checkPassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest,
                                        @AuthenticationPrincipal User user) {
        PasswordUpdateDto passwordUpdateDto = createPasswordUpdateDto(passwordUpdateRequest, user);
        userService.updatePassword(passwordUpdateDto);
        return response.success();
    }

    @DeleteMapping
    public ResponseEntity deleteUser(@AuthenticationPrincipal User user) {
        UserDeleteDto userDeleteDto = new UserDeleteDto(user.getId(), LocalDateTime.now());
        userService.deleteUser(userDeleteDto);
        // 200번이랑 202번이랑 고민됨
        return response.success();
    }

    private UserUpdateDto createUserUpdateDto(UserUpdateRequest userUpdateRequest, User user) {
        return UserUpdateDto.builder()
                .userEmail(user.getEmail())
                .profileImg(userUpdateRequest.profileImg())
                .birthDate(userUpdateRequest.birthDate())
                .name(userUpdateRequest.name())
                .build();
    }

    private PasswordUpdateDto createPasswordUpdateDto(PasswordUpdateRequest passwordUpdateRequest, User user) {
        return PasswordUpdateDto.builder()
                .userEmail(user.getEmail())
                .oldPassword(passwordUpdateRequest.oldPassword())
                .newPassword(passwordUpdateRequest.newPassword())
                .verifyPassword(passwordUpdateRequest.verifyPassword())
                .build();
    }

    @GetMapping("/schedules")
    public ResponseEntity<UserScheduleResponse> getSchedule(@AuthenticationPrincipal User user) {
        final List<UserScheduleDto> scheduleByUserId = userService.getScheduleByUserId(user.getId());
        UserScheduleResponse userScheduleResponse = new UserScheduleResponse(scheduleByUserId, scheduleByUserId.size());
        return response.success(userScheduleResponse);
    }
}
