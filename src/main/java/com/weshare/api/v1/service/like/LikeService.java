package com.weshare.api.v1.service.like;

import com.weshare.api.v1.controller.like.dto.CreateLikeDto;
import com.weshare.api.v1.controller.like.dto.DeleteLikeDto;
import com.weshare.api.v1.domain.schedule.like.Like;
import com.weshare.api.v1.domain.schedule.like.exception.DuplicateLikeException;
import com.weshare.api.v1.domain.schedule.like.exception.LikeNotFoundException;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.repository.like.LikeRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class LikeService {
    private final LikeRepository likeRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public Slice<FindAllScheduleLikeDto> findAllScheduleLike(Long scheduleId, Pageable pageable) {
        final Slice<Like> allLikeBySchedule = likeRepository.findAllLikeBySchedule(scheduleId, pageable);

        return allLikeBySchedule.map(this::getScheduleLikeDto);
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

        likeRepository.findLikeByUser(createLikeDto.liker())
                .ifPresent((like -> {
                    throw new DuplicateLikeException();}));

        final Like like = createLike(createLikeDto, findSchedule.getId());
        Like savedLike = likeRepository.save(like);
        return getCreateLikeResponse(savedLike);
    }

    private CreateLikeResponse getCreateLikeResponse(Like savedLike) {
        return new CreateLikeResponse(
                savedLike.getId(),
                savedLike.getUser().getName(),
                savedLike.getCreatedDate());
    }

    private Like createLike(CreateLikeDto createLikeDto, Long scheduleId) {
        return Like.builder()
                .user(createLikeDto.liker())
                .scheduleId(scheduleId)
                .build();
    }

    public void deleteScheduleLike(DeleteLikeDto deleteLikeDto) {
        final Like like = likeRepository.findById(deleteLikeDto.likeId())
                .orElseThrow(LikeNotFoundException::new);

        if (!like.isSameLiker(deleteLikeDto.liker())) {
            throw new IllegalArgumentException("사용자가 올바르지 않습니다.");
        }
        if (!like.isSameScheduleId(deleteLikeDto.scheduleId())) {
            throw new IllegalArgumentException("여행일정이 올바르지 않습니다.");
        }

        likeRepository.delete(like);
    }
}
