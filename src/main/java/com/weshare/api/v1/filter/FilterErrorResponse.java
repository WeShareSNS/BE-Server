package com.weshare.api.v1.filter;

import lombok.Builder;
import org.springframework.util.Assert;

@Builder
public record FilterErrorResponse(String uri, int code, String message) {
    public FilterErrorResponse {
        Assert.hasText(uri, "uri 값을 확인해주세요");
        Assert.hasText(message, "message 값을 확인해주세요");
    }
}
