package com.weshare.api.v1.service.like;

import com.weshare.api.v1.controller.like.dto.CreateLikeDto;
import com.weshare.api.v1.controller.like.dto.DeleteLikeDto;
import com.weshare.api.v1.domain.schedule.like.ScheduleLike;
import com.weshare.api.v1.domain.schedule.like.exception.DuplicateLikeException;
import com.weshare.api.v1.domain.schedule.like.exception.LikeNotFoundException;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.repository.like.ScheduleLikeRepository;
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
    private final ScheduleLikeRepository likeRepository;
    private final ScheduleRepository scheduleRepository;

    @Transactional(readOnly = true)
    public Slice<FindAllScheduleLikeDto> findAllScheduleLike(Long scheduleId, Pageable pageable) {
        final Slice<ScheduleLike> allLikeBySchedule = likeRepository.findAllLikeBySchedule(scheduleId, pageable);

        return allLikeBySchedule.map(this::getScheduleLikeDto);
    }

    private FindAllScheduleLikeDto getScheduleLikeDto(ScheduleLike scheduleLike) {
        return new FindAllScheduleLikeDto(
                scheduleLike.getId(),
                scheduleLike.getUser().getName(),
                scheduleLike.getCreatedDate());
    }

    public CreateLikeResponse saveScheduleLike(CreateLikeDto createLikeDto) {
        final Schedule findSchedule = scheduleRepository.findById(createLikeDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        likeRepository.findLikeByUser(createLikeDto.liker())
                .ifPresent((like -> {
                    throw new DuplicateLikeException();}));

        final ScheduleLike scheduleLike = createLike(createLikeDto, findSchedule.getId());
        ScheduleLike savedScheduleLike = likeRepository.save(scheduleLike);
        return getCreateLikeResponse(savedScheduleLike);
    }

    private CreateLikeResponse getCreateLikeResponse(ScheduleLike savedScheduleLike) {
        return new CreateLikeResponse(
                savedScheduleLike.getId(),
                savedScheduleLike.getUser().getName(),
                savedScheduleLike.getCreatedDate());
    }

    private ScheduleLike createLike(CreateLikeDto createLikeDto, Long scheduleId) {
        return ScheduleLike.builder()
                .user(createLikeDto.liker())
                .scheduleId(scheduleId)
                .build();
    }

    public void deleteScheduleLike(DeleteLikeDto deleteLikeDto) {
        final ScheduleLike scheduleLike = likeRepository.findById(deleteLikeDto.likeId())
                .orElseThrow(LikeNotFoundException::new);

        if (!scheduleLike.isSameLiker(deleteLikeDto.liker())) {
            throw new IllegalArgumentException("사용자가 올바르지 않습니다.");
        }
        if (!scheduleLike.isSameScheduleId(deleteLikeDto.scheduleId())) {
            throw new IllegalArgumentException("여행일정이 올바르지 않습니다.");
        }

        likeRepository.delete(scheduleLike);
    }
}
