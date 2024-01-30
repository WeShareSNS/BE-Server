package com.weShare.api.v1.domain;

public enum Social {
    KAKAO("kakao"),
    NAVER("naver"),
    GOOGLE("google"),
    DEFAULT("default");

    private String providerName;

    Social(String providerName) {
        this.providerName = providerName;
    }

    public String getProviderName() {
        return providerName;
    }
}
