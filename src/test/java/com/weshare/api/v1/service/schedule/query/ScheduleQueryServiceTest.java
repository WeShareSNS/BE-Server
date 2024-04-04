package com.weshare.api.v1.service.schedule.query;

import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import com.weshare.api.v1.service.schedule.query.dto.ScheduleDetailDto;
import com.weshare.api.v1.service.schedule.query.dto.SchedulePageDto;
import com.weshare.api.v1.service.schedule.query.dto.UserScheduleDto;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleQueryServiceTest extends ScheduleTestSupport {

    @Autowired
    private ScheduleQueryService scheduleQueryService;

    @Test
    @Transactional
    public void 특정_여행일정을_조회할_수_있다() {
        // given
        String userName = "test1";
        User user = createUserAndSave("test1@asd.com", userName, "test1");
        Destination destination = Destination.SEOUL;
        String title = "제목";
        Schedule schedule = createAndSaveSchedule(title, destination, user);
        Long scheduleId = schedule.getId();
        // when
        ScheduleDetailDto findScheduleDetails = scheduleQueryService.getScheduleDetails(scheduleId);
        // then
        assertThat(findScheduleDetails.getId()).isEqualTo(scheduleId);
        assertThat(findScheduleDetails.getTitle()).isEqualTo(title);
        assertThat(findScheduleDetails.getUserName()).isEqualTo(userName);
    }

    @Test
    @Transactional
    public void 여행일정_페이지는_최신글_순으로_조회된다() {
        // given
        ScheduleIds scheduleIds = getIdsAndSaveSchedule();
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("createdDate").descending());
        // when
        Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(pageRequest);
        List<SchedulePageDto> content = schedulePage.getContent();
        // then
        assertThat(content)
                .hasSize(2)
                .extracting("scheduleId", "expense", "userName", "likesCount", "commentsCount")
                .containsExactly(
                        Tuple.tuple(scheduleIds.scheduleIdLast(), 9000L, "test2", 1L, 1L),
                        Tuple.tuple(scheduleIds.scheduleIdFirst(), 9000L, "test1", 1L, 1L)
                );
    }

    @Transactional
    @TestFactory
    Collection<DynamicTest> 현재_페이지가_처음인지_마지막인지_알_수_있다() {
        // given
        ScheduleIds scheduleIds = getIdsAndSaveSchedule();
        return List.of(
                DynamicTest.dynamicTest("첫 페이지인지 알 수 있다.", () -> {
                    //given
                    Pageable pageRequest = PageRequest.of(0, 1, Sort.by("createdDate").descending());
                    // when
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(pageRequest);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    // then
                    assertThat(content)
                            .hasSize(1)
                            .extracting("scheduleId", "expense", "userName", "likesCount", "commentsCount")
                            .containsExactly(
                                    Tuple.tuple(scheduleIds.scheduleIdLast(), 9000L, "test2", 1L, 1L)
                            );
                    assertThat(schedulePage.getTotalPages()).isEqualTo(2);
                    assertThat(schedulePage.isFirst()).isEqualTo(true);
                    assertThat(schedulePage.isLast()).isEqualTo(false);
                }),
                DynamicTest.dynamicTest("마지막 페이지인지 알 수 있다.", () -> {
                    //given
                    Pageable pageRequest = PageRequest.of(1, 1, Sort.by("createdDate").descending());
                    // when
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(pageRequest);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    // then
                    assertThat(content)
                            .hasSize(1)
                            .extracting("scheduleId", "expense", "userName", "likesCount", "commentsCount")
                            .containsExactly(
                                    Tuple.tuple(scheduleIds.scheduleIdFirst(), 9000L, "test1", 1L, 1L)
                            );
                    assertThat(schedulePage.getTotalPages()).isEqualTo(2);
                    assertThat(schedulePage.isFirst()).isEqualTo(false);
                    assertThat(schedulePage.isLast()).isEqualTo(true);
                })
        );
    }

    @Test
    @Transactional
    public void 사용자가_작성한_게시물을_조회할_수_있다() {
        // given
        User user = createUserAndSave("test13@test.com", "test13", "password");
        createAndSaveSchedule("title1", Destination.BUSAN, user);
        createAndSaveSchedule("title2", Destination.GANGNEUNG, user);
        // when
        List<UserScheduleDto> allScheduleByUserId = scheduleQueryService.findAllScheduleByUserId(user.getId());
        // then
        assertThat(allScheduleByUserId).hasSize(2);
    }
}