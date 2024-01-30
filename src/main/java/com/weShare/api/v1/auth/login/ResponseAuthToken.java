package com.weShare.api.v1.auth.login;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ResponseAuthToken(@JsonProperty("access_token") String accessToken,
                                @JsonProperty("refresh_token")String refreshToken) {
}
