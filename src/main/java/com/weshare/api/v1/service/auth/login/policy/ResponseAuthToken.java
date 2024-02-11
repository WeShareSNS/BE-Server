package com.weshare.api.v1.service.auth.login.policy;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseAuthToken(@JsonProperty("access_token") String accessToken,
                                @JsonProperty("refresh_token")String refreshToken) {
}
