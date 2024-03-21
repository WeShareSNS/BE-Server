package com.weshare.api.v1.controller.comment;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.controller.comment.dto.*;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.comment.CommentService;
import com.weshare.api.v1.service.comment.CreateCommentResponse;
import com.weshare.api.v1.service.comment.FindAllCommentDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "comment-controller", description = "여행일정 댓글 controller")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip/schedules")
public class CommentController {

    private final CommentService commentService;
    private final Response response;

    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "사용자 댓글 등록 API", description = "사용자는 특정 여행일정에 댓글을 남길 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "댓글 등록에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "요청이 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "해당하는 여행일정이 존재하지 않습니다.")
    })
    @PostMapping("/{scheduleId}/comments")
    public ResponseEntity<CreateCommentResponse> saveScheduleComment(
            @PathVariable Long scheduleId,
            @AuthenticationPrincipal User commenter,
            @Valid @RequestBody CreateCommentRequest createCommentRequest
    ) {
        final CreateCommentDto createCommentDto = new CreateCommentDto(commenter, scheduleId, createCommentRequest.content());
        CreateCommentResponse createCommentResponse = commentService.saveScheduleComment(createCommentDto);

        return response.success(createCommentResponse, "댓글 등록 성공", HttpStatus.CREATED);
    }

    @Operation(summary = "여행일정 댓글 조회 API", description = "사용자는 특정 여행일정에 모든 댓글을 조회할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 조회에 성공했습니다."),
    })
    @GetMapping("/{scheduleId}/comments")
    public Slice<FindAllCommentDto> findAllScheduleComment(
            @PathVariable Long scheduleId,
            @PageableDefault(sort = "createdDate", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return commentService.findAllScheduleComment(scheduleId, pageable);
    }

    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "여행일정 댓글 수정 API", description = "사용자는 특정 여행일정에 달린 댓글을 수정할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 수정에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "해당하는 여행일정 혹은 댓글이 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "수정할 댓글이 없습니다."),
    })
    @PutMapping("/{scheduleId}/comments/{commentId}")
    public ResponseEntity updateScheduleComment(
            @AuthenticationPrincipal User commenter,
            @Valid @RequestBody UpdateCommentRequest updateCommentRequest,
            @PathVariable Long scheduleId,
            @PathVariable Long commentId
    ) {
        final UpdateCommentDto updateCommentDto =
                new UpdateCommentDto(commenter, updateCommentRequest.content(), scheduleId, commentId);
        commentService.updateComment(updateCommentDto);

        return response.success("댓글 수정 성공");
    }

    @Operation(security = {@SecurityRequirement(name = "bearer-key")},
            summary = "여행일정 댓글 삭제 API", description = "사용자는 특정 여행일정에 달린 댓글을 삭제할 수 있다.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "댓글 삭제에 성공했습니다."),
            @ApiResponse(responseCode = "400", description = "해당하는 여행일정 혹은 댓글이 올바르지 않습니다."),
            @ApiResponse(responseCode = "404", description = "삭제할 댓글이 없습니다."),
    })
    @DeleteMapping("/{scheduleId}/comments/{commentId}")
    public ResponseEntity deleteScheduleComment(
            @AuthenticationPrincipal User commenter,
            @PathVariable Long scheduleId,
            @PathVariable Long commentId
    ) {
        final DeleteCommentDto deleteCommentDto = new DeleteCommentDto(commenter, scheduleId, commentId);
        commentService.deleteScheduleComment(deleteCommentDto);

        return response.success("댓글 삭제 성공");
    }
}
