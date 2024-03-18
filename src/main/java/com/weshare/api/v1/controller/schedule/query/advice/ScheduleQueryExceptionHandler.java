package com.weshare.api.v1.controller.schedule.query.advice;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.weshare.api.v1.controller.schedule.query.advice.ScheduleQueryErrorCode.SCHEDULE_NOT_FOUND_ERROR;


@Slf4j
@RestControllerAdvice(basePackages = "com.weshare.api.v1.controller.schedule.query")
@RequiredArgsConstructor
public class ScheduleQueryExceptionHandler {

    private final Response response;

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity scheduleNotFoundHandler (ScheduleNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(SCHEDULE_NOT_FOUND_ERROR.getCode(), HttpStatus.NOT_FOUND, SCHEDULE_NOT_FOUND_ERROR.getMessage());
    }

}
