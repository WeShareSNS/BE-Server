package com.weshare.api.v1.service.like;

import com.weshare.api.v1.domain.like.Like;
import com.weshare.api.v1.repository.like.LikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;

    @Transactional(readOnly = true)
    public List<FindAllScheduleLikeDto> findAllScheduleLike(Long scheduleId) {
        final List<Like> allLikeBySchedule = likeRepository.findAllLikeBySchedule(scheduleId);

        return allLikeBySchedule.stream()
                .map(this::getScheduleLikeDto)
                .toList();
    }

    private FindAllScheduleLikeDto getScheduleLikeDto(Like like) {
        return new FindAllScheduleLikeDto(
                like.getId(),
                like.getUser().getName(),
                like.getCreatedDate());
    }
}
