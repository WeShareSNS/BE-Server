package com.weshare.api.v1.controller.like;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.controller.like.dto.CreateLikeDto;
import com.weshare.api.v1.controller.like.dto.DeleteLikeDto;
import com.weshare.api.v1.controller.like.dto.FindAllScheduleLikeResponse;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.like.CreateLikeResponse;
import com.weshare.api.v1.service.like.FindAllScheduleLikeDto;
import com.weshare.api.v1.service.like.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "like-controller", description = "여행일정 좋아요 controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip/schedules")
public class LikeController {

    private final Response response;
    private final LikeService likeService;

    @Operation(summary = "사용자 좋아요 조회 API", description = "사용자는 특정 여행일정에 좋아요 정보를 확인할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "좋아요 조회에 성공했습니다."),
    })
    @GetMapping("/{scheduleId}/likes")
    public Slice<FindAllScheduleLikeDto> getAllScheduleLike(@PathVariable Long scheduleId,
                                                            @PageableDefault Pageable pageable) {

        return likeService.findAllScheduleLike(scheduleId, pageable);
    }

    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "사용자 좋아요 등록 API", description = "사용자는 특정 여행일정에 좋아요를 등록할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "좋아요 등록에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "요청이 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "해당하는 여행일정이 존재하지 않습니다."),
            @ApiResponse(responseCode = "409", description = "이미 좋아요를 등록한 사용자 입니다.")
    })
    @PostMapping("/{scheduleId}/likes")
    public ResponseEntity<CreateLikeResponse> saveScheduleLike(@PathVariable Long scheduleId,
                                                               @AuthenticationPrincipal User liker) {
        final CreateLikeDto createLikeDto = new CreateLikeDto(scheduleId, liker);
        CreateLikeResponse createLikeResponse = likeService.saveScheduleLike(createLikeDto);

        return response.success(createLikeResponse);
    }

    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "여행일정 댓글 삭제 API", description = "사용자는 특정 여행일정에 달린 댓글을 삭제할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "해당하는 여행일정 혹은 사용자가 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "취소할 좋아요가 없습니다."),
    })
    @DeleteMapping("/{scheduleId}/likes/{likeId}")
    public void deleteScheduleLike(@PathVariable Long scheduleId,
                                   @PathVariable Long likeId,
                                   @AuthenticationPrincipal User liker) {
        final DeleteLikeDto deleteLikeDto = new DeleteLikeDto(scheduleId, likeId, liker);
        likeService.deleteScheduleLike(deleteLikeDto);
    }
}
