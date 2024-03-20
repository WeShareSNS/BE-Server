package com.weshare.api.v1.controller.comment;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.comment.CommentService;
import com.weshare.api.v1.service.comment.CreateCommentResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "comment-controller", description = "여행일정 댓글 controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip/schedules")
public class CommentController {

    private final CommentService commentService;
    private final Response response;

    @Operation(security = { @SecurityRequirement(name = "bearer-key") },
            summary = "사용자 댓글 등록 API", description = "사용자는 특정 여행일정에 댓글을 남길 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 등록에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "요청이 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "해당하는 여행일정이 존재하지 않습니다.")
    })
    @PostMapping("/{scheduleId}/comments")
    public ResponseEntity<CreateCommentResponse> saveScheduleComment(@PathVariable Long scheduleId,
                                                                     @AuthenticationPrincipal User user,
                                                                     @Valid @RequestBody CreateCommentRequest createCommentRequest) {

        CreateCommentDto createCommentDto = new CreateCommentDto(user, scheduleId, createCommentRequest.content());
        CreateCommentResponse createCommentResponse = commentService.saveScheduleComment(createCommentDto);
        return response.success(createCommentResponse, "댓글 등록 성공", HttpStatus.CREATED);
    }
}
