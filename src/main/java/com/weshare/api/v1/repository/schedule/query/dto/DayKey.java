package com.weshare.api.v1.repository.schedule.query.dto;

import java.time.LocalDate;

public record DayKey(Long dayId, LocalDate travelDate) {
}
