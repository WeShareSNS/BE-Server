package com.weShare.api.v1.auth.kakao;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseAuthToken(@JsonProperty("access_token") String accessToken,
                                @JsonProperty("refresh_token")String refreshToken) {
}