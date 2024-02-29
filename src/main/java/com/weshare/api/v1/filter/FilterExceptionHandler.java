package com.weshare.api.v1.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;

@Component
public class FilterExceptionHandler {

    public void handleAuthenticationExceptionMessage(
            HttpServletRequest request,
            HttpServletResponse response,
            HttpStatus status,
            FilterErrorCode error
    ) throws IOException {

        response.setStatus(status.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        PrintWriter writer = response.getWriter();

        FilterErrorResponse errorResponse = FilterErrorResponse.builder()
                .uri(request.getRequestURI())
                .code(error.getCode())
                .message(error.getMessage())
                .build();

        ObjectMapper mapper = new ObjectMapper();
        String errorJson = mapper.writeValueAsString(errorResponse);

        writer.write(errorJson);
        writer.flush();
    }
}
