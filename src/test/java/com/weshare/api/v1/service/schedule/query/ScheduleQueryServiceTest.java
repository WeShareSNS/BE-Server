package com.weshare.api.v1.service.schedule.query;

import com.weshare.api.v1.controller.schedule.query.SearchScheduleDto;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.init.statistics.InitStatisticsScheduleDetails;
import com.weshare.api.v1.init.statistics.InitStatisticsScheduleTotalCount;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import com.weshare.api.v1.service.schedule.query.dto.ScheduleDetailDto;
import com.weshare.api.v1.service.schedule.query.dto.ScheduleFilterPageDto;
import com.weshare.api.v1.service.schedule.query.dto.SchedulePageDto;
import com.weshare.api.v1.service.schedule.query.dto.UserScheduleDto;
import org.assertj.core.api.Assertions;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;

class ScheduleQueryServiceTest extends ScheduleTestSupport {

    @Autowired
    private ScheduleQueryService scheduleQueryService;
    @Autowired
    private InitStatisticsScheduleTotalCount initStatisticsScheduleTotalCount;
    @Autowired
    private InitStatisticsScheduleDetails initStatisticsScheduleDetails;

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
        FindScheduleDetailDto findScheduleDetailDto = new FindScheduleDetailDto(scheduleId, Optional.ofNullable(user).map(User::getId));
        ScheduleDetailDto findScheduleDetails = scheduleQueryService.getScheduleDetails(findScheduleDetailDto);
        // then
        assertThat(findScheduleDetails.getScheduleId()).isEqualTo(scheduleId);
        assertThat(findScheduleDetails.getTitle()).isEqualTo(title);
        assertThat(findScheduleDetails.getUserName()).isEqualTo(userName);
    }

    @Test
    @Transactional
    public void 여행일정_페이지는_최신글_순으로_조회된다() {
        // given
        ScheduleIds scheduleIds = getIdsAndSaveSchedule();
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("created-date").descending());
        // when
        ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder().pageable(pageRequest).build();
        Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
        List<SchedulePageDto> content = schedulePage.getContent();
        // then
        assertThat(content)
                .hasSize(2)
                .extracting("scheduleId", "userName")
                .containsExactly(
                        tuple(scheduleIds.scheduleIdLast(), "test2"),
                        tuple(scheduleIds.scheduleIdFirst(), "test1")
                );
    }

    @Test
    @Transactional
    public void 목적지로_여행일정을_조회할_수_있다() {
        // given
        User user = createUserAndSave("test14@test.com", "test14", "password");
        Destination destination = Destination.GYEONGGI;
        Schedule schedule1 = createAndSaveSchedule("제목1", destination, user);
        createAndSaveSchedule("제목2", Destination.SEOUL, user);
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("created-date").descending());
        // when
        ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder()
                .destinations(Set.of(destination.getName()))
                .pageable(pageRequest)
                .build();
        Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
        List<SchedulePageDto> content = schedulePage.getContent();
        // then
        assertThat(content)
                .hasSize(1)
                .extracting("scheduleId", "title", "userName", "destination")
                .containsExactly(tuple(schedule1.getId(), schedule1.getTitle(), schedule1.getUser().getName(), destination));
    }

    @Transactional
    @DisplayName("여행일정 총 비용이 9000원일 때 여행비용을 통해서 조회할 수 있다.")
    @TestFactory
    Collection<DynamicTest> 여행비용_범위로_여행일정을_조회할_수_있다() {
        // given
        User user = createUserAndSave("test14@test.com", "test14", "password");
        Schedule schedule = createAndSaveSchedule("제목1", Destination.GYEONGGI, user);
        initStatisticsScheduleDetails.init();
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("created-date").descending());
        return List.of(
                DynamicTest.dynamicTest("9000~10000원을 지정하면 조회시 조회가 가능하다.", () -> {
                    //given
                    String expenseCondition = "9000~10000";
                    //when
                    ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder()
                            .pageable(pageRequest)
                            .expenseCondition(expenseCondition)
                            .build();
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    //then
                    Assertions.assertThat(schedule.getTotalScheduleExpense()).isEqualTo(9000L);
                    assertThat(content)
                            .hasSize(1)
                            .extracting("scheduleId", "title", "userName", "destination")
                            .containsExactly(
                                    tuple(schedule.getId(), schedule.getTitle(), schedule.getUser().getName(), schedule.getDestination())
                            );
                }),
                DynamicTest.dynamicTest("0~9000원을 사이를 지정하면 조회가 가능하다.", () -> {
                    //given
                    String expenseCondition = "~9000";
                    //when
                    ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder()
                            .pageable(pageRequest)
                            .expenseCondition(expenseCondition)
                            .build();
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    //then
                    Assertions.assertThat(schedule.getTotalScheduleExpense()).isEqualTo(9000L);
                    assertThat(content)
                            .hasSize(1)
                            .extracting("scheduleId", "title", "userName", "destination")
                            .containsExactly(
                                    tuple(schedule.getId(), schedule.getTitle(), schedule.getUser().getName(), schedule.getDestination())
                            );
                }),
                DynamicTest.dynamicTest("0~범위 끝까지 지정하면 조회가 가능하다.", () -> {
                    //given
                    String expenseCondition = "~";
                    //when
                    ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder()
                            .pageable(pageRequest)
                            .expenseCondition(expenseCondition)
                            .build();
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    //then
                    Assertions.assertThat(schedule.getTotalScheduleExpense()).isEqualTo(9000L);
                    assertThat(content)
                            .hasSize(1)
                            .extracting("scheduleId", "title", "userName", "destination")
                            .containsExactly(
                                    tuple(schedule.getId(), schedule.getTitle(), schedule.getUser().getName(), schedule.getDestination())
                            );
                })
        );
    }

    @Transactional
    @DisplayName("여행일정 총 비용이 9000원일 때 범위를 넘어가면 여행비용을 통해서 조회할 수 없다.")
    @TestFactory
    Collection<DynamicTest> 여행비용_범위를_넘어가면_여행일정을_조회할_수_없다() {
        // given
        User user = createUserAndSave("test14@test.com", "test14", "password");
        Schedule schedule = createAndSaveSchedule("제목1", Destination.GYEONGGI, user);
        initStatisticsScheduleDetails.init();
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("created-date").descending());
        return List.of(
                DynamicTest.dynamicTest("9500~범위 끝까지 사이를 지정하면 조회되지 않는다.", () -> {
                    //given
                    String expenseCondition = "9500~";
                    //when
                    ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder()
                            .pageable(pageRequest)
                            .expenseCondition(expenseCondition)
                            .build();
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    //then
                    Assertions.assertThat(schedule.getTotalScheduleExpense()).isEqualTo(9000L);
                    assertThat(content.isEmpty()).isTrue();
                }),
                DynamicTest.dynamicTest("0~8999원 사이를 지정하면 조회되지 않는다.", () -> {
                    //given
                    String expenseCondition = "~8999";
                    //when
                    ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder()
                            .pageable(pageRequest)
                            .expenseCondition(expenseCondition)
                            .build();
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    //then
                    Assertions.assertThat(schedule.getTotalScheduleExpense()).isEqualTo(9000L);
                    assertThat(content.isEmpty()).isTrue();
                }),
                DynamicTest.dynamicTest("5000~8900원 사이를 지정하면 조회되지 않는다.", () -> {
                    //given
                    String expenseCondition = "5000~8900";
                    //when
                    ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder()
                            .pageable(pageRequest)
                            .expenseCondition(expenseCondition)
                            .build();
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    //then
                    Assertions.assertThat(schedule.getTotalScheduleExpense()).isEqualTo(9000L);
                    assertThat(content.isEmpty()).isTrue();
                })
        );
    }

    @Transactional
    @TestFactory
    Collection<DynamicTest> 현재_페이지가_처음인지_마지막인지_알_수_있다() {
        // given
        ScheduleIds scheduleIds = getIdsAndSaveSchedule();
        initStatisticsScheduleTotalCount.init();
        return List.of(
                DynamicTest.dynamicTest("첫 페이지인지 알 수 있다.", () -> {
                    //given
                    Pageable pageRequest = PageRequest.of(0, 1, Sort.by("created-date").descending());
                    // when
                    ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder().pageable(pageRequest).build();
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    // then
                    assertThat(content)
                            .hasSize(1)
                            .extracting("scheduleId", "userName")
                            .containsExactly(
                                    tuple(scheduleIds.scheduleIdLast(), "test2")
                            );
                    assertThat(schedulePage.getTotalPages()).isEqualTo(2);
                    assertThat(schedulePage.isFirst()).isEqualTo(true);
                    assertThat(schedulePage.isLast()).isEqualTo(false);
                }),
                DynamicTest.dynamicTest("마지막 페이지인지 알 수 있다.", () -> {
                    //given
                    Pageable pageRequest = PageRequest.of(1, 1, Sort.by("created-date").descending());
                    // when
                    ScheduleFilterPageDto scheduleFilterPageDto = ScheduleFilterPageDto.builder().pageable(pageRequest).build();
                    Page<SchedulePageDto> schedulePage = scheduleQueryService.getSchedulePage(scheduleFilterPageDto);
                    List<SchedulePageDto> content = schedulePage.getContent();
                    // then
                    assertThat(content)
                            .hasSize(1)
                            .extracting("scheduleId", "userName")
                            .containsExactly(
                                    tuple(scheduleIds.scheduleIdFirst(), "test1")
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
        createAndSaveSchedule("title1", Destination.SEOUL, user);
        createAndSaveSchedule("title2", Destination.JEJU, user);
        // when
        List<UserScheduleDto> allScheduleByUserId = scheduleQueryService.findAllScheduleByUserId(user.getId());
        // then
        assertThat(allScheduleByUserId).hasSize(2);
    }

    @Test
    @Transactional
    public void 검색을_통해서_게시물을_조회할_수_있다() {
        // given
        User user = createUserAndSave("test13@test.com", "test13", "password");
        createAndSaveSchedule("title1", Destination.SEOUL, user);
        String findTitle = "title2";
        createAndSaveSchedule(findTitle, Destination.JEJU, user);
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("created-date").descending());
        // when
        ScheduleSearchCondition scheduleSearchCondition = new ScheduleSearchCondition(user.getId(), findTitle, pageRequest);
        List<SearchScheduleDto> searchSchedules = scheduleQueryService.searchSchedule(scheduleSearchCondition)
                .getContent();
        // then
        assertThat(searchSchedules).hasSize(1)
                .extracting("title", "destination")
                .containsExactly(Tuple.tuple(findTitle, Destination.JEJU));
    }

    @Test
    @Transactional
    public void 검색에_해당하는_게시물이_없을_수_있다() {
        // given
        User user = createUserAndSave("test13@test.com", "test13", "password");
        createAndSaveSchedule("title1", Destination.SEOUL, user);
        createAndSaveSchedule("title1", Destination.JEJU, user);
        Pageable pageRequest = PageRequest.of(0, 2, Sort.by("created-date").descending());
        // when
        String notContentTitle = "메롱";
        ScheduleSearchCondition scheduleSearchCondition = new ScheduleSearchCondition(user.getId(), notContentTitle, pageRequest);
        List<SearchScheduleDto> searchSchedules = scheduleQueryService.searchSchedule(scheduleSearchCondition)
                .getContent();
        // then
        assertThat(searchSchedules).hasSize(0);
    }
}