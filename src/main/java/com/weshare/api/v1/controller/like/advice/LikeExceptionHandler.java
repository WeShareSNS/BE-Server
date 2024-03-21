package com.weshare.api.v1.controller.like.advice;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.controller.comment.CommentErrorCode;
import com.weshare.api.v1.domain.comment.exception.CommentNotFoundException;
import com.weshare.api.v1.domain.like.exception.LikeNotFoundException;
import com.weshare.api.v1.domain.schedule.exception.ScheduleNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.weshare.api.v1.controller.like.LikeErrorCode.LIKE_NOT_FOUND_ERROR;
import static com.weshare.api.v1.controller.like.LikeErrorCode.SCHEDULE_NOT_FOUND_ERROR;

@Slf4j
@RestControllerAdvice(basePackages = "")
@RequiredArgsConstructor
public class LikeExceptionHandler {

    private final Response response;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler (IllegalArgumentException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(CommentErrorCode.BAD_REQUEST_ERROR.getCode(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity illegalStateExceptionHandler (IllegalStateException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(CommentErrorCode.BAD_REQUEST_ERROR.getCode(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(ScheduleNotFoundException.class)
    public ResponseEntity scheduleNotFoundExceptionHandler (ScheduleNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(SCHEDULE_NOT_FOUND_ERROR.getCode(), HttpStatus.NOT_FOUND, SCHEDULE_NOT_FOUND_ERROR.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(LikeNotFoundException.class)
    public ResponseEntity likeNotFoundExceptionHandler (LikeNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(LIKE_NOT_FOUND_ERROR.getCode(), HttpStatus.NOT_FOUND, LIKE_NOT_FOUND_ERROR.getMessage());
    }
}
