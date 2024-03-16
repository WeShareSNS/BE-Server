package com.weshare.api.v1.service.schedule.query;

import com.weshare.api.v1.domain.schedule.Schedule;
import com.weshare.api.v1.repository.schedule.query.ScheduleDetailQueryRepository;
import com.weshare.api.v1.repository.schedule.query.SchedulePageQueryRepository;
import com.weshare.api.v1.service.schedule.query.dto.SchedulePageDto;
import com.weshare.api.v1.service.schedule.query.dto.ScheduleDetailDto;
import com.weshare.api.v1.repository.schedule.query.dto.SchedulePageFlatDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScheduleQueryService {

    private final SchedulePageQueryRepository pageQueryRepository;
    private final ScheduleDetailQueryRepository detailQueryRepository;

    public Page<SchedulePageDto> getSchedulePage(Pageable pageable) {
        Page<SchedulePageFlatDto> schedulePage = pageQueryRepository.findSchedulePage(pageable);
        return schedulePage.map(SchedulePageDto::from);
    }

    public ScheduleDetailDto getScheduleDetails(Long scheduleId) {
        if (scheduleId == null) {
            throw new IllegalArgumentException("게시물에 접근할 수 없습니다.");
        }
        final Schedule scheduleDetail = detailQueryRepository.findScheduleDetail(scheduleId);
        // service에서 변환하지 않고 dto 정적 메서드 이용하기
        return ScheduleDetailDto.from(scheduleDetail);
    }
}
