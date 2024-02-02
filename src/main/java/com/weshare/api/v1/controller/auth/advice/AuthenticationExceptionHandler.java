package com.weshare.api.v1.controller.auth.advice;

import com.weshare.api.v1.service.auth.login.OAuthApiException;
import com.weshare.api.v1.domain.user.exception.EmailDuplicateException;
import com.weshare.api.v1.token.exception.InvalidTokenException;
import com.weshare.api.v1.token.exception.TokenNotFoundException;
import com.weshare.api.v1.token.exception.TokenTimeOutException;
import com.weshare.api.v1.common.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import static com.weshare.api.v1.controller.auth.AuthErrorCode.*;

@Slf4j
@RestControllerAdvice(basePackages = "com.weShare.api.v1.auth")
@RequiredArgsConstructor
public class AuthenticationExceptionHandler {

    private final Response response;

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity illegalArgumentExceptionHandler (IllegalArgumentException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(BAD_REQUEST_ERROR.getCode(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(OAuthApiException.class)
    public ResponseEntity oAuthApiExceptionHandler (OAuthApiException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(AUTH_LOGIN_BAD_REQUEST.getCode(), HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity methodArgumentNotValidExceptionHandler (MethodArgumentNotValidException  e){
        log.error("[exceptionHandler] ex", e);
        String[] errorMessages = getDefaultErrorMessage(e);
        return response.fail(PARAMETER_BAD_REQUEST_ERROR.getCode(), HttpStatus.BAD_REQUEST, errorMessages);
    }

    private String[] getDefaultErrorMessage(BindException e) {
        return e.getBindingResult().getFieldErrors()
                .stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .toArray(String[]::new);
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenTimeOutException.class)
    public ResponseEntity tokenTimeOutExceptionHandler (TokenTimeOutException  e, WebRequest request){
        log.error("[exceptionHandler] ex", e);
        return response.fail(TOKEN_TIME_OUT_ERROR.getCode(), HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(TokenNotFoundException.class)
    public ResponseEntity tokenNotFoundExceptionHandler (TokenNotFoundException  e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(TOKEN_NOT_FOUND_ERROR.getCode(), HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity invalidTokenExceptionHandler (InvalidTokenException  e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(INVALID_TOKEN_ERROR.getCode(), HttpStatus.UNAUTHORIZED, e.getMessage());
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity usernameNotFoundHandler (UsernameNotFoundException e){
        log.error("[exceptionHandler] ex", e);
        return response.fail(USER_NOT_FOUND_ERROR.getCode(), HttpStatus.NOT_FOUND, e.getMessage());
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EmailDuplicateException.class)
    public ResponseEntity EmailDuplicateExceptionHandler (EmailDuplicateException e) {
        log.error("[exceptionHandler] ex", e);
        return response.fail(EMAIL_DUPLICATE_ERROR.getCode(), HttpStatus.CONFLICT, e.getMessage());
    }


    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ResponseEntity exHandler(Exception e) {
        log.error("[exceptionHandler] ex", e);
        return response.fail(SERVER_ERROR.getCode(), HttpStatus.INTERNAL_SERVER_ERROR, "[server error] " + getDefaultErrorMessage((BindException) e));
    }
}
