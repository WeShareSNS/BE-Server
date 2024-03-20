package com.weshare.api.v1.controller.comment.advice;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.controller.comment.CommentErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static com.weshare.api.v1.controller.comment.CommentErrorCode.SCHEDULE_NOT_FOUND_ERROR;


@Slf4j
@RestControllerAdvice(basePackages = "com.weshare.api.v1.controller.comment")
@RequiredArgsConstructor
public class CommentExceptionHandler {

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
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity usernameNotFoundHandler (UsernameNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(SCHEDULE_NOT_FOUND_ERROR.getCode(), HttpStatus.NOT_FOUND, SCHEDULE_NOT_FOUND_ERROR.getMessage());
    }
}
