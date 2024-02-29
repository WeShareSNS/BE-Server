package com.weshare.api.v1.domain.schedule;

import java.util.Arrays;

public enum Destination {
    SEOUL("서울"),
    BUSAN("부산"),
    Gangneung("강릉");
//
    private final String name;

    Destination(String name) {
        this.name = name;
    }

    public static Destination findDestinationByName(String name) {
        return Arrays.stream(Destination.values())
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("목적지 이름이 올바르지 않습니다."));
    }

}
