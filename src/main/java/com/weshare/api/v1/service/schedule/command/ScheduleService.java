package com.weshare.api.v1.service.schedule.command;

import com.weshare.api.v1.controller.schedule.command.dto.CreateScheduleDto;
import com.weshare.api.v1.controller.schedule.command.dto.UpdateScheduleDto;
import com.weshare.api.v1.domain.schedule.Day;
import com.weshare.api.v1.domain.schedule.Destination;
import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import com.weshare.api.v1.domain.user.User;
import com.weshare.api.v1.event.schedule.ScheduleCreatedEvent;
import com.weshare.api.v1.event.schedule.ScheduleUpdatedEvent;
import com.weshare.api.v1.repository.schedule.DayRepository;
import com.weshare.api.v1.repository.schedule.ScheduleRepository;
import com.weshare.api.v1.repository.schedule.query.ScheduleQueryRepository;
import com.weshare.api.v1.service.exception.AccessDeniedModificationException;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ScheduleService {

    private final ScheduleQueryRepository scheduleQueryRepository;
    private final DayRepository dayRepository;
    private final ScheduleRepository scheduleRepository;
    private final ApplicationEventPublisher eventPublisher;

    public Schedule saveSchedule(final CreateScheduleDto createScheduleDto) {
        Schedule schedule = createScheduleDto.toEntity();
        schedule.initDays();
        Schedule save = scheduleRepository.save(schedule);
        eventPublisher.publishEvent(new ScheduleCreatedEvent(schedule.getId(), schedule.getTotalScheduleExpense()));
        return save;
    }

    public void updateSchedule(UpdateScheduleDto updateScheduleDto) {
        Optional<List<Day>> updateDays = updateScheduleDto.toDayEntity();

        /**
         * days 랑 fetch join하면 변경감지가 수행이 안되고 save 호출시 CaseCade 옵션 때문인지 days가 전부 지워지면서 문제가 발생한다.
         * 지연로딩을 이용해서 schedule만 가져오기
         */
        Schedule findSchedule = scheduleRepository.findById(updateScheduleDto.getScheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        User user = findSchedule.getUser();
        if (!user.isSameId(updateScheduleDto.getUserId())) {
            throw new AccessDeniedModificationException();
        }
        Destination destination = Destination.findDestinationByName(updateScheduleDto.getDestination());
        String title = updateScheduleDto.getTitle()
                .orElse(findSchedule.getTitle());

        findSchedule.updateDestinationOrTitle(destination, title);
        updateDays.ifPresent(u -> updateScheduleDays(findSchedule, u));
    }

    private void updateScheduleDays(Schedule findSchedule, List<Day> updateDays) {
        if (!findSchedule.isContainDays(updateDays)) {
            throw new IllegalArgumentException("날짜 정보가 올바르지 않습니다.");
        }

        final List<Long> dayIds = getDayIds(updateDays);
        final List<Day> findDays = dayRepository.findDayByIds(dayIds);
        final Map<Long, Day> dayMap = getDayMap(findDays);

        updateDays(updateDays, dayMap);
        eventPublisher.publishEvent(new ScheduleUpdatedEvent(findSchedule.getId()));
    }

    private List<Long> getDayIds(List<Day> updateDays) {
        return updateDays.stream()
                .map(Day::getId)
                .toList();
    }

    private Map<Long, Day> getDayMap(List<Day> findDays) {
        return findDays.stream()
                .collect(Collectors.toMap(Day::getId, Function.identity()));
    }

    private void updateDays(List<Day> updateDays, Map<Long, Day> dayMap) {
        updateDays.forEach(u ->
                Optional.ofNullable(dayMap.get(u.getId()))
                        .ifPresent(d -> d.updatePlaces(u)));
    }

    public void deleteSchedule(DeleteScheduleDto deleteScheduleDto) {
        Schedule schedule = scheduleQueryRepository.findScheduleDetailById(deleteScheduleDto.scheduleId())
                .orElseThrow(ScheduleNotFoundException::new);

        User user = schedule.getUser();
        if (!user.isSameId(deleteScheduleDto.userId())) {
            throw new AccessDeniedModificationException();
        }

        scheduleRepository.delete(schedule);
    }
}
