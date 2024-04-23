package com.weshare.api.v1.controller.like;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.controller.like.dto.*;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.like.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "like-controller", description = "여행일정 좋아요 controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip")
public class LikeController {

    private final Response response;
    private final LikeService likeService;

    @Operation(summary = "여행 일정 좋아요 조회 API", description = "사용자는 특정 여행일정에 좋아요 정보를 확인할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 조회에 성공했습니다."),
    })
    @GetMapping("/schedules/{scheduleId}/likes")
    public Slice<FindAllScheduleLikeDto> getAllScheduleLike(@PathVariable Long scheduleId,
                                                            @PageableDefault Pageable pageable) {

        return likeService.findAllScheduleLike(scheduleId, pageable);
    }

    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "여행 일정 좋아요 API", description = "사용자는 특정 여행일정에 좋아요를 등록할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "좋아요 등록에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "요청이 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "해당하는 여행일정이 존재하지 않습니다."),
            @ApiResponse(responseCode = "409", description = "이미 좋아요를 등록한 사용자 입니다.")
    })
    @PostMapping("/schedules/{scheduleId}/likes")
    public ResponseEntity<CreateScheduleLikeResponse> saveScheduleLike(@PathVariable Long scheduleId,
                                                                       @AuthenticationPrincipal User liker) {
        final CreateScheduleLikeDto createScheduleLikeDto = new CreateScheduleLikeDto(scheduleId, liker);
        CreateScheduleLikeResponse createScheduleLikeResponse = likeService.saveScheduleLike(createScheduleLikeDto);

        return response.success(createScheduleLikeResponse);
    }

    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "여행 일정 좋아요 삭제 API", description = "사용자는 여행일정에 등록했던 좋아요를 삭제할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 삭제에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "해당하는 여행일정 혹은 사용자가 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "취소할 좋아요가 없습니다."),
    })
    @DeleteMapping("/schedules/{scheduleId}/likes/{likeId}")
    public void deleteScheduleLike(@PathVariable Long scheduleId,
                                   @PathVariable Long likeId,
                                   @AuthenticationPrincipal User liker) {
        final DeleteScheduleLikeDto deleteScheduleLikeDto = new DeleteScheduleLikeDto(scheduleId, likeId, liker);
        likeService.deleteScheduleLike(deleteScheduleLikeDto);
    }

    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "댓글 좋아요 API", description = "사용자는 특정 댓글에 좋아요를 등록할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "좋아요 등록에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "요청이 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "해당하는 여행일정이 존재하지 않습니다."),
            @ApiResponse(responseCode = "409", description = "이미 좋아요를 등록한 사용자 입니다.")
    })
    @PostMapping("/comments/{commentId}/likes")
    public ResponseEntity<CreateCommentLikeResponse> saveCommentLike(
            @PathVariable Long commentId,
            @AuthenticationPrincipal User liker
    ) {
        final CreateCommentLikeDto createCommentLikeDto = new CreateCommentLikeDto(commentId, liker);
        CreateCommentLikeResponse createScheduleLikeResponse = likeService.saveCommentLike(createCommentLikeDto);

        return response.success(createScheduleLikeResponse);
    }
}
