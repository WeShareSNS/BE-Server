package com.weshare.api.v1.repository.schedule.query;

import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import com.weshare.api.v1.repository.schedule.query.dto.SchedulePageDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

class SchedulePageQueryRepositoryImplTest extends ScheduleTestSupport {
    @Autowired
    private SchedulePageQueryRepository schedulePageQueryRepository;

    @Test
    @Transactional
    @DisplayName("페이지는 최신글 순으로 조회된다.")
    public void 여행일정_페이지를_조회() {
        // given
        createTwoScheduleAndSaveAll();
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("createdDate").descending());
        // when
        Page<SchedulePageDto> schedulePage = schedulePageQueryRepository.findSchedulePage(pageRequest);
        List<SchedulePageDto> content = schedulePage.getContent();
        // then
        Assertions.assertThat(content)
                .hasSize(2)
                .extracting("scheduleId", "expense", "username", "likesCount", "commentsCount")
                .containsExactly(
                        Tuple.tuple(2L, 18000L, "test2", 0L, 0L),
                        Tuple.tuple(1L, 9000L, "test1", 1L, 1L)
                );
    }

}