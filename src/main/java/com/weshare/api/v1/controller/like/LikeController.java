package com.weshare.api.v1.controller.like;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.domain.like.Like;
import com.weshare.api.v1.service.like.FindAllScheduleLikeDto;
import com.weshare.api.v1.service.like.LikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/trip/schedules")
public class LikeController {

    private final Response response;
    private final LikeService likeService;

    @GetMapping("/{scheduleId}/likes")
    public ResponseEntity<List<Like>> getAllScheduleLike(@PathVariable Long scheduleId) {
        final List<FindAllScheduleLikeDto> allLikes = likeService.findAllScheduleLike(scheduleId);
        FindAllScheduleLikeResponse findAllScheduleLikeResponse =
                new FindAllScheduleLikeResponse(allLikes, allLikes.size());

        return response.success(findAllScheduleLikeResponse);
    }

}
