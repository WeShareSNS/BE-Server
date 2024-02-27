package com.weshare.api.v1.domain.schedule;

public enum Destination {
    SEOUL("서울"),
    BUSAN("부산"),
    Gangneung("강릉");
//
    private final String name;

    Destination(String name) {
        this.name = name;
    }
}
