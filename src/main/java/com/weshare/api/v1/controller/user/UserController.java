package com.weshare.api.v1.controller.user;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.controller.user.dto.DeleteUserRequest;
import com.weshare.api.v1.controller.user.dto.PasswordUpdateRequest;
import com.weshare.api.v1.controller.user.dto.UserUpdateRequest;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.schedule.query.dto.UserScheduleDto;
import com.weshare.api.v1.service.user.UserService;
import com.weshare.api.v1.service.user.dto.PasswordUpdateDto;
import com.weshare.api.v1.service.user.dto.UserDeleteDto;
import com.weshare.api.v1.service.user.dto.UserUpdateDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "user-controller", description = "마이페이지 컨트롤러")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/me")
public class UserController {

    private final Response response;
    private final UserService userService;

    @Operation(summary = "사용자 정보 수정 api", description = "비밀번호와 이메일을 제외한 사용자 정보를 수정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 정보 수정 성공"),
            @ApiResponse(responseCode = "400", description = "request body 를 확인해주세요"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
    })
    @PatchMapping
    public ResponseEntity updateUser(@Valid @RequestBody(required = false) UserUpdateRequest userUpdateRequest,
                                     @AuthenticationPrincipal User user) {
        UserUpdateDto updateDto = createUserUpdateDto(userUpdateRequest, user);
        userService.updateUser(updateDto);
        return response.success();
    }

    @Operation(summary = "사용자 비밀번호 수정 api", description = "사용자의 비밀번호를 수정할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 비밀번호 수정 성공"),
            @ApiResponse(responseCode = "400", description = "request body 를 확인해주세요"),
            @ApiResponse(responseCode = "404", description = "사용자를 찾을 수 없습니다."),
    })
    @PatchMapping("/password")
    public ResponseEntity checkPassword(@Valid @RequestBody PasswordUpdateRequest passwordUpdateRequest,
                                        @AuthenticationPrincipal User user) {
        PasswordUpdateDto passwordUpdateDto = createPasswordUpdateDto(passwordUpdateRequest, user);
        userService.updatePassword(passwordUpdateDto);
        return response.success();
    }

    @Operation(summary = "사용자 탈퇴 api", description = "사용자가 기록한 모든 데이터가 같이 삭제됩니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "사용자 탈퇴 성공"),
    })
    @DeleteMapping
    public ResponseEntity deleteUser(@Valid @RequestBody DeleteUserRequest deleteUserRequest,
                                     @AuthenticationPrincipal User user) {
        UserDeleteDto userDeleteDto = new UserDeleteDto(user.getId(), deleteUserRequest.password(), LocalDateTime.now());
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

    @Operation(summary = "내가 작성한 여행일정 조회 api", description = "사용자가 작성한 여행일정을 조회할 수 있습니다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "여행일정 조회 성공")
    })
    @GetMapping("/schedules")
    public ResponseEntity<UserScheduleResponse> getSchedule(@AuthenticationPrincipal User user) {
        final List<UserScheduleDto> scheduleByUserId = userService.getScheduleByUserId(user.getId());
        UserScheduleResponse userScheduleResponse = new UserScheduleResponse(scheduleByUserId, scheduleByUserId.size());
        return response.success(userScheduleResponse);
    }
}
