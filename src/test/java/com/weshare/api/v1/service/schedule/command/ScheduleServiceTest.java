package com.weshare.api.v1.service.schedule.command;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weshare.api.v1.controller.schedule.command.CreateScheduleRequest;
import com.weshare.api.v1.domain.schedule.Day;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import com.weshare.api.v1.repository.schedule.query.ScheduleQueryRepository;
import com.weshare.api.v1.service.exception.AccessDeniedModificationException;
import org.assertj.core.groups.Tuple;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ScheduleServiceTest extends ScheduleTestSupport {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ScheduleQueryRepository queryRepository;

    @Test
    // user 생성시 no session 때문에 생성
    @Transactional
    public void 여행일정을_저장할_수_있다() throws IOException {
        // given
        User user = createUserAndSave("test@asdf.com","asdfa","password");
        CreateScheduleRequest request = objectMapper.readValue(getRequestJson(), CreateScheduleRequest.class);
        CreateScheduleDto createScheduleDto = CreateScheduleDto.of(request, user);
        // when
        Schedule schedule = scheduleService.saveSchedule(createScheduleDto);
        Long scheduleId = schedule.getId();
        // then
        Schedule findSchedule = scheduleRepository.findById(scheduleId).orElseThrow();
        assertThat(findSchedule.getUser()).isEqualTo(user);
        assertThat(findSchedule.getTitle()).isEqualTo("여행 일정");
        assertThat(findSchedule.getDestination()).isEqualTo(Destination.SEOUL);
        assertThat(findSchedule.getStartDate()).isEqualTo(LocalDate.of(2024, 12, 3));
        assertThat(findSchedule.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 5));
    }

    private String getRequestJson() {
        return """
                {
                    "title": "여행 일정",
                    "destination": "서울",
                    "startDate": "2024-12-03",
                    "endDate": "2024-12-05",
                    "dayDetail": [
                      {
                        "travelDate": "2024-12-03",
                        "places": [
                          {
                            "memo": "이쁘다~",
                            "time": "10:00 AM",
                            "title": "첫 번째 장소 목적지 이름",
                            "expense": 100,
                            "latitude": "37.1234",
                            "longitude": "127.5678"
                          },
                          {
                            "memo": "이쁘다~",
                            "time": "01:00 PM",
                            "title": "두 번째 장소",
                            "expense": 60000,
                            "latitude": "37.5678",
                            "longitude": "127.9876"
                          }
                        ]
                      },
                      {
                        "travelDate": "2024-12-04",
                        "places": [
                          {
                            "memo": "이쁘다~",
                            "time": "11:00 PM",
                            "title": "세 번째 장소",
                            "expense": 9900000,
                            "latitude": "37.2468",
                            "longitude": "127.8765"
                          }
                        ]
                      },
                      {
                        "travelDate": "2024-12-05",
                        "places": [
                          {
                            "memo": "이쁘다~",
                            "time": "11:00 AM",
                            "title": "네 번째 장소",
                            "expense": 124,
                            "latitude": "37.2468",
                            "longitude": "127.8765"
                          }
                        ]
                      }
                    ]
                }
                """;
    }

    @Test
    @Transactional
    public void 여행일정_제목과_목직지를_수정할_수_있다() {
        // given
        User user = createUserAndSave("test20@test.com", "test20", "password");
        Schedule schedule = createAndSaveSchedule("제목 입니다.", Destination.CHUNGCHEONG, user);
        // when
        String updateTitle = "업데이트된 제목입니다.";
        Destination updateDestination = Destination.GYEONGGI;
        UpdateScheduleDto updateScheduleDto = UpdateScheduleDto.builder()
                .userId(user.getId())
                .scheduleId(schedule.getId())
                .title(updateTitle)
                .visitDates(Optional.empty())
                .destination("경기")
                .build();
        scheduleService.updateSchedule(updateScheduleDto);
        // then
        Schedule updateSchedule = scheduleRepository.findById(schedule.getId()).orElseThrow();
        assertThat(updateSchedule).extracting("title", "destination")
                .containsExactlyInAnyOrder(updateTitle, updateDestination);
    }

    @Test
    @Transactional
    public void 여행일정_날짜들을_수정할_수_있다() {
        // given
        User user = createUserAndSave("test20@test.com", "test20", "password");
        Schedule schedule = createAndSaveSchedule("제목 입니다.", Destination.CHUNGCHEONG, user);
        List<UpdateScheduleDto.UpdateDayDto> updateDayDtos = schedule.getDays().stream()
                .map(this::updateDay)
                .toList();

        // when
        UpdateScheduleDto updateScheduleDto = UpdateScheduleDto.builder()
                .userId(user.getId())
                .scheduleId(schedule.getId())
                .visitDates(Optional.of(updateDayDtos))
                .build();
        scheduleService.updateSchedule(updateScheduleDto);
        // then
        Schedule updateSchedule = queryRepository.findScheduleDetailById(schedule.getId()).orElseThrow();
        List<Day> days = updateSchedule.getDays();
        // 테스트는 한 가지만 테스트하기 위해서 반복문 테스트는 안하는데 안에있는 정보들이 수정되었는지 확인하기 위해서 반복문 테스트로 진행
        days.forEach(day -> {
            assertThat(day.getPlaces())
                    .hasSize(1)
                    .extracting("title", "expense", "longitude", "latitude")
                    // place가 하나만 존재하기 때문에 totalExpense로 조회
                    .containsExactly(Tuple.tuple("메롱롱", day.getTotalDayExpense(), 30.25, 36.25));
        });
    }

    private UpdateScheduleDto.UpdateDayDto updateDay(Day day) {
        LocalDate travelDate = day.getTravelDate();
        Long id = day.getId();
        return UpdateScheduleDto.UpdateDayDto.builder()
                .dayId(id)
                .travelDate(travelDate)
                .visitPlaces(List.of(createPlace()))
                .build();
    }

    private UpdateScheduleDto.UpdateDayDto.UpdatePlaceDto createPlace() {
        return UpdateScheduleDto.UpdateDayDto.UpdatePlaceDto.builder()
                .time(LocalTime.of(3, 12))
                .title("메롱롱")
                .longitude(30.25)
                .latitude(36.25)
                .expense(1000)
                .build();
    }

    @Test
    @Transactional
    public void 여행일정을_삭제할_수_있다() {
        // given
        User user = createUserAndSave("test20@test.com", "test20", "password");
        Schedule schedule = createAndSaveSchedule("제목 입니다.", Destination.CHUNGCHEONG, user);
        // when
        DeleteScheduleDto deleteScheduleDto = new DeleteScheduleDto(user.getId(), schedule.getId());
        scheduleService.deleteSchedule(deleteScheduleDto);
        // then
        Optional<Schedule> deletedSchedule = scheduleRepository.findById(schedule.getId());
        assertThat(deletedSchedule.isEmpty()).isTrue();
    }

    @Test
    @Transactional
    public void 존재하지_않는_여행일정을_삭제하면_예외가_발생한다() {
        // given
        User user = createUserAndSave("test20@test.com", "test20", "password");
        Schedule schedule = createAndSaveSchedule("제목 입니다.", Destination.CHUNGCHEONG, user);
        DeleteScheduleDto deleteScheduleDto = new DeleteScheduleDto(user.getId(), schedule.getId());
        // when // then
        scheduleService.deleteSchedule(deleteScheduleDto);
        assertThatThrownBy(() -> scheduleService.deleteSchedule(deleteScheduleDto))
                .isInstanceOf(ScheduleNotFoundException.class);
    }

    @Test
    @Transactional
    public void 다른_사용자가_여행일정을_삭제하면_예외가_발생한다() {
        // given
        User user = createUserAndSave("test20@test.com", "test20", "password");
        User otherUser = createUserAndSave("other@test.com", "other", "password");
        Schedule schedule = createAndSaveSchedule("제목 입니다.", Destination.CHUNGCHEONG, user);
        DeleteScheduleDto deleteScheduleDto = new DeleteScheduleDto(otherUser.getId(), schedule.getId());
        // when // then
        assertThatThrownBy(() -> scheduleService.deleteSchedule(deleteScheduleDto))
                .isInstanceOf(AccessDeniedModificationException.class);
    }

}