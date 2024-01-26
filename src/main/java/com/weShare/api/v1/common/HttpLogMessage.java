package com.weShare.api.v1.common;

import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;


@NoArgsConstructor
public final class HttpLogMessage {

    //HTTP 헤더 필드는 HTTP 프록시 또는 로드 밸런서를 통해 웹 서버 에 연결하는 클라이언트의 원래 IP 주소를 식별하는 일반적인 방법입니다 .
    private static final String X_FORWARDED_FOR_HEADER_NAME = "X-FORWARDED-FOR";
    private String httpMethod;
    private String requestUri;
    private HttpStatus httpStatus;
    private String clientIp;
    private double elapsedTime;
    private Map<String, String> headers;
    private Map<String, String> requestParam;
    private String requestBody;
    private String responseBody;

    private HttpLogMessage(ContentCachingRequestWrapper requestWrapper,
                           ContentCachingResponseWrapper responseWrapper,
                           double elapsedTime) throws IOException {

        this.httpMethod = requestWrapper.getMethod();
        this.requestUri = requestWrapper.getRequestURI();
        this.httpStatus = HttpStatus.valueOf(responseWrapper.getStatus());
        this.clientIp = Optional.ofNullable(
                        requestWrapper.getHeader(X_FORWARDED_FOR_HEADER_NAME))
                .orElse(requestWrapper.getRemoteAddr());

        this.elapsedTime = elapsedTime;
        this.headers = getHeaders(requestWrapper);
        this.requestParam = getParameters(requestWrapper);
        this.requestBody = getRequestBody(requestWrapper);
        this.responseBody = getResponseBody(responseWrapper);
    }

    private Map<String, String> getHeaders(ContentCachingRequestWrapper request) {
        Map<String, String> headerMap = new HashMap<>();
        Enumeration<String> headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            headerMap.put(headerName, request.getHeader(headerName));
        }
        return headerMap;
    }

    private Map<String, String> getParameters(ContentCachingRequestWrapper request) {
        Map<String, String> parameterMap = new HashMap<>();
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String headerName = parameterNames.nextElement();
            parameterMap.put(headerName, request.getParameter(headerName));
        }
        return parameterMap;
    }

    private String getRequestBody(ContentCachingRequestWrapper request){
        byte[] contentAsByteArray = request.getContentAsByteArray();
        if (contentAsByteArray.length > 0) {
            try {
                return new String(contentAsByteArray, request.getCharacterEncoding());
            } catch (UnsupportedEncodingException e) {
                return "{}";
            }
        }
        return "{}";
    }

    private String getResponseBody(ContentCachingResponseWrapper response) throws IOException {
        String payload = null;
        byte[] contentAsByteArray = response.getContentAsByteArray();
        if (contentAsByteArray.length > 0) {
            payload = new String(contentAsByteArray, response.getCharacterEncoding());
            response.copyBodyToResponse();
        }
        return payload != null ? payload : "{}";
    }

    public static HttpLogMessage createInstance(
            ContentCachingRequestWrapper requestWrapper,
            ContentCachingResponseWrapper responseWrapper,
            double elapsedTime
    ) throws IOException {

        requestWrapper.setCharacterEncoding("UTF-8");
        responseWrapper.setCharacterEncoding("UTF-8");
        return new HttpLogMessage(requestWrapper, responseWrapper, elapsedTime);
    }

    public String toPrettierLog() {
        String logFormat = """
                [REQUEST] %s %s %s (%.3f)
                >> CLIENT_IP: %s
                >> HEADERS: %s
                >> REQUEST_PARAM: %s
                >> REQUEST_BODY: %s
                >> RESPONSE_BODY: %s"
                """;
        return String.format(logFormat,
                this.httpMethod, this.requestUri, this.httpStatus, this.elapsedTime,
                this.clientIp,
                this.headers,
                this.requestParam,
                this.requestBody,
                this.responseBody
        );
    }
}