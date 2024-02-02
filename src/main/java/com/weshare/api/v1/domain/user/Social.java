package com.weshare.api.v1.domain.user;

import lombok.Getter;

public enum Social {
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google"),
    DEFAULT("default");

    @Getter
    private String providerName;

    Social(String providerName) {
        this.providerName = providerName;
    }
}
