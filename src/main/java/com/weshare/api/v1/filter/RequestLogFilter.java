package com.weshare.api.v1.filter;

import com.weshare.api.v1.common.CustomUUID;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.jboss.logging.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;

import java.io.IOException;

@Slf4j
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
@WebFilter(filterName = "RequestCachingFilter", urlPatterns = "/*")
public class RequestLogFilter extends OncePerRequestFilter {
    private static final String REQUEST_ID = "request_id";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        long start = System.currentTimeMillis();

        ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(response);

        String requestId = CustomUUID.getCustomUUID(8);
        MDC.put(REQUEST_ID, requestId);

        filterChain.doFilter(requestWrapper, responseWrapper);

        long end = System.currentTimeMillis();
        log.info(
                HttpLogMessage.createInstance(
                        requestWrapper,
                        responseWrapper,
                        (double) (end - start) / 1000).toPrettierLog()
        );
        MDC.remove(REQUEST_ID);
    }
}
