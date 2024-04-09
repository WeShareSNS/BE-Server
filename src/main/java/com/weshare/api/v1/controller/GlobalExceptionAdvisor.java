package com.weshare.api.v1.controller;

import com.weshare.api.v1.common.Response;
import com.weshare.api.v1.service.exception.AccessDeniedModificationException;
import com.weshare.api.v1.token.exception.InvalidTokenException;
import com.weshare.api.v1.token.exception.TokenNotFoundException;
import com.weshare.api.v1.token.exception.TokenTimeOutException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;


@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionAdvisor {

    private final Response response;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity methodArgumentNotValidExceptionHandler (MethodArgumentNotValidException  e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(ErrorCode.PARAMETER_BAD_REQUEST_ERROR.getCode(), HttpStatus.BAD_REQUEST, getDefaultErrorMessage(e));
    }

    private String getDefaultErrorMessage(BindException e) {
        BindingResult bindingResult = e.getBindingResult();

        return bindingResult.getFieldErrors().stream()
                .map(fieldError -> "[" + fieldError.getField() + "](은)는 " +
                        fieldError.getDefaultMessage() + " 입력된 값: [" +
                        fieldError.getRejectedValue() + "]")
                .collect(Collectors.joining());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenTimeOutException.class)
    public ResponseEntity tokenTimeOutExceptionHandler (TokenTimeOutException  e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(ErrorCode.TOKEN_TIME_OUT_ERROR.getCode(), HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_TIME_OUT_ERROR.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity tokenNotFoundExceptionHandler (TokenNotFoundException  e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(ErrorCode.TOKEN_NOT_FOUND_ERROR.getCode(), HttpStatus.UNAUTHORIZED, ErrorCode.TOKEN_NOT_FOUND_ERROR.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity invalidTokenExceptionHandler (InvalidTokenException  e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(ErrorCode.INVALID_TOKEN_ERROR.getCode(), HttpStatus.UNAUTHORIZED, ErrorCode.INVALID_TOKEN_ERROR.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedModificationException.class)
    public ResponseEntity accessDeniedModificationExceptionHandler (AccessDeniedModificationException  e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(ErrorCode.ACCESS_DENIED_ERROR.getCode(), HttpStatus.FORBIDDEN, ErrorCode.ACCESS_DENIED_ERROR.getMessage());
    }
}
