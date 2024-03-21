package com.weshare.api.v1.controller.like;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.service.like.CreateLikeResponse;
import com.weshare.api.v1.service.like.FindAllScheduleLikeDto;
import com.weshare.api.v1.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip/schedules")
public class LikeController {

    private final Response response;
    private final LikeService likeService;

    @GetMapping("/{scheduleId}/likes")
    public ResponseEntity<FindAllScheduleLikeResponse> getAllScheduleLike(@PathVariable Long scheduleId) {
        final List<FindAllScheduleLikeDto> allLikes = likeService.findAllScheduleLike(scheduleId);
        FindAllScheduleLikeResponse findAllScheduleLikeResponse =
                new FindAllScheduleLikeResponse(allLikes, allLikes.size());

        return response.success(findAllScheduleLikeResponse);
    }

    @PostMapping("/{scheduleId}/likes")
    public ResponseEntity<CreateLikeResponse> saveScheduleLike(@PathVariable Long scheduleId,
                                           @AuthenticationPrincipal User liker) {
        final CreateLikeDto createLikeDto = new CreateLikeDto(scheduleId, liker);
        CreateLikeResponse createLikeResponse = likeService.saveScheduleLike(createLikeDto);

        return response.success(createLikeResponse);
    }

}
