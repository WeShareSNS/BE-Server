package com.weshare.api.v1.controller;

import com.weshare.api.v1.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

import static com.weshare.api.v1.controller.auth.AuthErrorCode.*;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ExceptionAdvisor {

    private final Response response;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler (IllegalArgumentException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(BAD_REQUEST_ERROR.getCode(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity methodArgumentNotValidExceptionHandler (MethodArgumentNotValidException  e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(PARAMETER_BAD_REQUEST_ERROR.getCode(), HttpStatus.BAD_REQUEST, getDefaultErrorMessage(e));
    }

    private String getDefaultErrorMessage(BindException e) {
        BindingResult bindingResult = e.getBindingResult();

        return bindingResult.getFieldErrors().stream()
                .map(fieldError -> "[" + fieldError.getField() + "](은)는 " +
                        fieldError.getDefaultMessage() + " 입력된 값: [" +
                        fieldError.getRejectedValue() + "]")
                .collect(Collectors.joining());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseEntity exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return response.fail(SERVER_ERROR.getCode(), HttpStatus.INTERNAL_SERVER_ERROR, "[server error] " + getDefaultErrorMessage((BindException) e));
    }
}
