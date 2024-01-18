package com.weShare.api.v1.common;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class Response<T> {

    @Getter
    @Builder
    private static class SuccessBody<T> {
        private int state;
        private T data;
        private String message;
    }

    public ResponseEntity<SuccessBody<T>> success(T data, String msg, HttpStatus status) {
        SuccessBody<T> body = SuccessBody.<T>builder()
                .state(status.value())
                .data(data)
                .message(msg)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<SuccessBody<T>> success(String msg) {
        return success(null, msg, HttpStatus.OK);
    }
    public ResponseEntity<SuccessBody<T>> success(T data) {
        return success(data, null, HttpStatus.OK);
    }
    public ResponseEntity<T> success() {
        return (ResponseEntity<T>) success(null, null, HttpStatus.OK);
    }


    @Getter
    @Builder
    private static class FailBody<T> {

        private int state;
        private String code;
        private T data;
        private String message;
    }

    public ResponseEntity<FailBody<T>> fail(T data, String code, String msg, HttpStatus status) {
        FailBody<T> body = FailBody.<T>builder()
                .state(status.value())
                .code(code)
                .data(data)
                .message(msg)
                .build();
        return ResponseEntity.status(status).body(body);
    }

    public ResponseEntity<FailBody<T>> fail(String code, String msg, HttpStatus status) {
        return fail(null, code, msg, status);
    }
}
