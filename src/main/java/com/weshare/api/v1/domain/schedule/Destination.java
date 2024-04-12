package com.weshare.api.v1.domain.schedule;

import lombok.Getter;

import java.util.Arrays;
import java.util.Objects;

public enum Destination {

    EMPTY(null),
    SEOUL("서울"),
    GYEONGGI("경기"),
    GANGWON("강원도"),
    CHUNGCHEONG("충청도"),
    JEOLLA("전라도"),
    GYEONGSANG("경상도"),
    JEJU("제주도");

    @Getter
    private final String name;

    Destination(String name) {
        this.name = name;
    }

    public static Destination findDestinationByNameOrElseThrow(String name) {
        return Arrays.stream(Destination.values())
                .filter(d -> Objects.equals(d.name, name))
                .findAny()
                .orElseThrow(() -> new IllegalArgumentException("목적지 이름이 올바르지 않습니다."));
    }

    public static Destination findDestinationByName(String name) {
        return Arrays.stream(Destination.values())
                .filter(d -> Objects.equals(d.name, name))
                .findAny()
                .orElse(EMPTY);
    }

    public boolean isEmpty() {
        return this == EMPTY;
    }

}
