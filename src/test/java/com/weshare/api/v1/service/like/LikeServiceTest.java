package com.weshare.api.v1.service.like;

import com.weshare.api.v1.controller.like.dto.CreateLikeDto;
import com.weshare.api.v1.controller.like.dto.DeleteLikeDto;
import com.weshare.api.v1.domain.like.exception.DuplicateLikeException;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class LikeServiceTest extends ScheduleTestSupport {

    @Autowired
    private LikeService likeService;

    @Test
    @Transactional
    public void 특정_게시물에_좋아요를_등록할_수_있다() {
        // given
        User user = createUserAndSave("like@test.com", "like1", "like");
        Schedule schedule = createAndSaveSchedule("title", Destination.DAEGU, user);
        CreateLikeDto createLikeDto = new CreateLikeDto(schedule.getId(), user);
        // when
        CreateLikeResponse createLikeResponse = likeService.saveScheduleLike(createLikeDto);
        // then
        assertThat(user.getName()).isEqualTo(createLikeResponse.likerName());
    }

    @Test
    @Transactional
    public void 사용자가_이미_좋아요를_등록한_여행일정에_좋아요를_등록하면_예외가_발생한다() {
        // given
        User user = createUserAndSave("like@test.com", "like1", "like");
        Schedule schedule = createAndSaveSchedule("title", Destination.DAEGU, user);
        CreateLikeDto createLikeDto = new CreateLikeDto(schedule.getId(), user);
        likeService.saveScheduleLike(createLikeDto);
        // when // then
        assertThatThrownBy(() -> likeService.saveScheduleLike(createLikeDto))
                .isInstanceOf(DuplicateLikeException.class);
    }

    @Test
    @Transactional
    public void 존재하지_않는_게시물에_좋아요_등록시_예외가_발생한다() {
        // given
        User user = createUserAndSave("like@test.com", "like1", "like");
        CreateLikeDto createLikeDto = new CreateLikeDto(0L, user);
        // when // then
        assertThatThrownBy(() -> likeService.saveScheduleLike(createLikeDto))
                .isInstanceOf(ScheduleNotFoundException.class);
    }

    @Test
    @Transactional
    public void 사용자는_특정_게시물에_등록한_좋아요를_삭제할_수_있다() {
        // given
        User user = createUserAndSave("like@test.com", "like1", "like");
        Schedule schedule = createAndSaveSchedule("title", Destination.DAEGU, user);
        CreateLikeDto createLikeDto = new CreateLikeDto(schedule.getId(), user);
        CreateLikeResponse createLikeResponse = likeService.saveScheduleLike(createLikeDto);
        // when
        DeleteLikeDto deleteLikeDto = new DeleteLikeDto(schedule.getId(), createLikeResponse.likeId(), user);
        likeService.deleteScheduleLike(deleteLikeDto);
        PageRequest pageRequest = PageRequest.of(0, 1);
        // then
        List<FindAllScheduleLikeDto> allScheduleLike = likeService.findAllScheduleLike(schedule.getId(),pageRequest).getContent();
        assertThat(allScheduleLike).hasSize(0);
    }

    @Test
    @Transactional
    public void 특정_게시물에_좋아요_정보를_확인할_수_있다() {
        // given
        User user = createUserAndSave("like@test.com", "like1", "like");
        Schedule schedule = createAndSaveSchedule("title", Destination.DAEGU, user);
        CreateLikeDto createLikeDto = new CreateLikeDto(schedule.getId(), user);
        CreateLikeResponse createLikeResponse = likeService.saveScheduleLike(createLikeDto);
        PageRequest pageRequest = PageRequest.of(0, 1);
        // when
        List<FindAllScheduleLikeDto> allScheduleLike = likeService.findAllScheduleLike(schedule.getId(),pageRequest).getContent();
        // then
        assertThat(allScheduleLike).hasSize(1)
                .extracting("likeId", "likerName", "likedTime")
                .containsExactly(
                        Tuple.tuple(createLikeResponse.likeId(), user.getName(), createLikeResponse.likedTime())
                );
    }
}