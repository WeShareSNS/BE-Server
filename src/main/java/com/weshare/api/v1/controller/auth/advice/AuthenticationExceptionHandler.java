package com.weshare.api.v1.controller.auth.advice;

import com.weshare.api.v1.controller.auth.AuthErrorCode;
import com.weshare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weshare.api.v1.domain.user.exception.UsernameDuplicateException;
import com.weshare.api.v1.service.auth.login.OAuthApiException;
import com.weshare.api.v1.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@Slf4j
@RestControllerAdvice(basePackages = "com.weshare.api.v1.controller.auth")
@RequiredArgsConstructor
public class AuthenticationExceptionHandler {

    private final Response response;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler (IllegalArgumentException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(AuthErrorCode.BAD_REQUEST_ERROR.getCode(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity illegalStateExceptionHandler (IllegalStateException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(AuthErrorCode.BAD_REQUEST_ERROR.getCode(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OAuthApiException.class)
    public ResponseEntity oAuthApiExceptionHandler (OAuthApiException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(AuthErrorCode.AUTH_LOGIN_BAD_REQUEST.getCode(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity usernameNotFoundHandler (UsernameNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(AuthErrorCode.USER_NOT_FOUND_ERROR.getCode(), HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity emailDuplicateExceptionHandler (EmailDuplicateException e) {
        log.error("[exceptionHandler] ex", e);
        return response.fail(AuthErrorCode.EMAIL_DUPLICATE_ERROR.getCode(), HttpStatus.CONFLICT, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(UsernameDuplicateException.class)
    public ResponseEntity usernameDuplicateExceptionHandler (UsernameDuplicateException e) {
        log.error("[exceptionHandler] ex", e);
        return response.fail(AuthErrorCode.NAME_DUPLICATE_ERROR.getCode(), HttpStatus.CONFLICT, e.getMessage());
    }
}
