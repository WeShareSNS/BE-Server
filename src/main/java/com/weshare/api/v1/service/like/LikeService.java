package com.weshare.api.v1.service.like;

import com.weshare.api.v1.controller.like.CreateLikeDto;
import com.weshare.api.v1.domain.like.Like;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.repository.like.LikeRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final ScheduleRepository scheduleRepository;

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

    public CreateLikeResponse saveScheduleLike(CreateLikeDto createLikeDto) {
        final Schedule findSchedule = scheduleRepository.findById(createLikeDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        final Like like = createLike(createLikeDto, findSchedule);
        Like savedLike = likeRepository.save(like);
        return getCreateLikeResponse(savedLike);
    }

    private CreateLikeResponse getCreateLikeResponse(Like savedLike) {
        return new CreateLikeResponse(
                savedLike.getId(),
                savedLike.getUser().getName(),
                savedLike.getCreatedDate());
    }

    private Like createLike(CreateLikeDto createLikeDto, Schedule findSchedule) {
        return Like.builder()
                .user(createLikeDto.liker())
                .schedule(findSchedule)
                .build();
    }
}
