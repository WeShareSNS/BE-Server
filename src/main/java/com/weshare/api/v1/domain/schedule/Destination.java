package com.weshare.api.v1.domain.schedule;

import java.util.Arrays;
import java.util.Objects;

public enum Destination {

    EMPTY(null),
    SEOUL("서울"),
    GYEONGGI("경기"),
    BUSAN("부산"),
    GANGNEUNG("강릉"),
    INCHEON("인천"),
    SUWON("수원"),
    DAEGU("대구"),
    DAEJEON("대전"),
    JEJU("제주"),
    GWANGJU("광주"),
    ULSAN("울산"),
    JEONJU("전주"),
    CHUNCHEON("춘천");
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

}
