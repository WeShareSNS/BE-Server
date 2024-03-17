package com.weshare.api.v1.repository.schedule.query;

import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import com.weshare.api.v1.repository.schedule.query.dto.SchedulePageFlatDto;
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

class SchedulePageQueryRepositoryImplTest extends ScheduleTestSupport {
    @Autowired
    private SchedulePageQueryRepository schedulePageQueryRepository;

    @Test
    @Transactional
    public void 여행일정이_존재하지_않으면_빈_값을_출력한다() {
        // given
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("createdDate").descending());
        // when
        Page<SchedulePageFlatDto> schedulePage = schedulePageQueryRepository.findSchedulePage(pageRequest);
        List<SchedulePageFlatDto> content = schedulePage.getContent();
        // then
        assertThat(content).hasSize(0);
        assertThat(schedulePage.getTotalPages()).isEqualTo(0);
        assertThat(schedulePage.isFirst()).isEqualTo(true);
        assertThat(schedulePage.isLast()).isEqualTo(true);
    }

    @Test
    @Transactional
    public void 여행일정_페이지는_최신글_순으로_조회된다() {
        // given
        for (int i = 1; i <= 2; i++) {
            User user = createUserAndSave(i + "test@asd.com", "test" + i, "test");
            Destination destination = Destination.SEOUL;
            String title = "제목" + i;
            Schedule schedule = createAndSaveSchedule(title, destination, user);
            createAndSaveLike(schedule.getId(), user.getId());
            createAndSaveComment(schedule.getId(), user.getId());
        }
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("createdDate").descending());
        // when
        Page<SchedulePageFlatDto> schedulePage = schedulePageQueryRepository.findSchedulePage(pageRequest);
        List<SchedulePageFlatDto> content = schedulePage.getContent();
        // then
        assertThat(content)
                .hasSize(2)
                .extracting("scheduleId", "title", "likesCount", "commentsCount")
                .containsExactly(
                        Tuple.tuple(2L, "제목2", 1L, 1L),
                        Tuple.tuple(1L, "제목1", 1L, 1L)
                );
    }

    @Transactional
    @TestFactory
    Collection<DynamicTest> 현재_페이지가_처음인지_마지막인지_알_수_있다() {
        // given
        for (int i = 1; i <= 2; i++) {
            User user = createUserAndSave(i + "test@asd.com", "test" + i, "test");
            Destination destination = Destination.SEOUL;
            String title = "제목" + i;
            Schedule schedule = createAndSaveSchedule(title, destination, user);
            createAndSaveLike(schedule.getId(), user.getId());
            createAndSaveComment(schedule.getId(), user.getId());
        }
        return List.of(
                DynamicTest.dynamicTest("첫 페이지인지 알 수 있다.", () -> {
                    //given
                    Pageable pageRequest = PageRequest.of(0, 1, Sort.by("createdDate").descending());
                    // when
                    Page<SchedulePageFlatDto> schedulePage = schedulePageQueryRepository.findSchedulePage(pageRequest);
                    List<SchedulePageFlatDto> content = schedulePage.getContent();
                    // then
                    assertThat(content)
                            .hasSize(1)
                            .extracting("scheduleId", "title", "likesCount", "commentsCount")
                            .containsExactly(
                                    Tuple.tuple(2L, "제목2", 1L, 1L)
                            );
                    assertThat(schedulePage.getTotalPages()).isEqualTo(2);
                    assertThat(schedulePage.isFirst()).isEqualTo(true);
                    assertThat(schedulePage.isLast()).isEqualTo(false);
                }),
                DynamicTest.dynamicTest("마지막 페이지인지 알 수 있다.", () -> {
                    //given
                    Pageable pageRequest = PageRequest.of(1, 1, Sort.by("createdDate").descending());
                    // when
                    Page<SchedulePageFlatDto> schedulePage = schedulePageQueryRepository.findSchedulePage(pageRequest);
                    List<SchedulePageFlatDto> content = schedulePage.getContent();
                    // then
                    assertThat(content)
                            .hasSize(1)
                            .extracting("scheduleId", "title", "likesCount", "commentsCount")
                            .containsExactly(
                                    Tuple.tuple(1L, "제목1", 1L, 1L)
                            );
                    assertThat(schedulePage.getTotalPages()).isEqualTo(2);
                    assertThat(schedulePage.isFirst()).isEqualTo(false);
                    assertThat(schedulePage.isLast()).isEqualTo(true);
                })
        );
    }
}