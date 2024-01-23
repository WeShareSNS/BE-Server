package com.weShare.api.v1.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ReissueTokenRequest {
    private String refreshToken;

    public ReissueTokenRequest(String refreshToken) {
        this.refreshToken = refreshToken;
    }
}
