package com.weshare.api.v1.controller.comment;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.comment.CommentService;
import com.weshare.api.v1.service.comment.CreateCommentResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip/schedules")
public class CommentController {

    private final CommentService commentService;
    private final Response response;
    @PostMapping("/{scheduleId}/comments")
    public ResponseEntity<CreateCommentResponse> saveScheduleComment(@PathVariable Long scheduleId,
                                                                     @AuthenticationPrincipal User user,
                                                                     @Valid @RequestBody CreateCommentRequest createCommentRequest) {

        CreateCommentDto createCommentDto = new CreateCommentDto(user, scheduleId, createCommentRequest.content());
        CreateCommentResponse createCommentResponse = commentService.saveScheduleComment(createCommentDto);
        return response.success(createCommentResponse, "댓글 등록 성공", HttpStatus.CREATED);
    }
}
