package com.weshare.api.v1.service.schedule;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.weshare.api.v1.controller.schedule.command.CreateScheduleDto;
import com.weshare.api.v1.controller.schedule.command.CreateScheduleRequest;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import com.weshare.api.v1.repository.schedule.ScheduleTestSupport;
import com.weshare.api.v1.repository.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class ScheduleServiceTest extends ScheduleTestSupport {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private ScheduleRepository scheduleRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Test
    // user 생성시 no session 때문에 생성
    @Transactional
    public void 여행일정을_저장할_수_있다() throws IOException {
        // given
        User user = createAndSaveUser();
        CreateScheduleRequest request = objectMapper.readValue(getRequestJson(), CreateScheduleRequest.class);
        CreateScheduleDto createScheduleDto = CreateScheduleDto.from(request);
        // when
        scheduleService.saveSchedule(createScheduleDto, user);
        // then
        Schedule schedule = scheduleRepository.findById(1L).orElseThrow();
        assertThat(schedule.getUser()).isEqualTo(user);
        assertThat(schedule.getTitle()).isEqualTo("여행 일정");
        assertThat(schedule.getDestination()).isEqualTo(Destination.SEOUL);
        assertThat(schedule.getStartDate()).isEqualTo(LocalDate.of(2024, 12, 3));
        assertThat(schedule.getEndDate()).isEqualTo(LocalDate.of(2024, 12, 5));
    }

    private User createAndSaveUser() {
        User user = User.builder()
                .email("asd@asdf.com")
                .name("test")
                .password("test")
                .build();
        return userRepository.save(user);
    }

    private String getRequestJson() {
        return """
                {
                    "title": "여행 일정",
                    "destination": "서울",
                    "startDate": "2024-12-03",
                    "endDate": "2024-12-05",
                    "visitDates": [
                      {
                        "travelDate": "2024-12-03",
                        "visitPlaces": [
                          {
                            "memo": "이쁘다~",
                            "time": "10:00",
                            "title": "첫 번째 장소 목적지 이름",
                            "expense": 100,
                            "latitude": "37.1234",
                            "longitude": "127.5678"
                          },
                          {
                            "memo": "이쁘다~",
                            "time": "13:00",
                            "title": "두 번째 장소",
                            "expense": 60000,
                            "latitude": "37.5678",
                            "longitude": "127.9876"
                          }
                        ]
                      },
                      {
                        "travelDate": "2024-12-04",
                        "visitPlaces": [
                          {
                            "memo": "이쁘다~",
                            "time": "11:00",
                            "title": "세 번째 장소",
                            "expense": 9900000,
                            "latitude": "37.2468",
                            "longitude": "127.8765"
                          }
                        ]
                      },
                      {
                        "travelDate": "2024-12-05",
                        "visitPlaces": [
                          {
                            "memo": "이쁘다~",
                            "time": "11:00",
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

}