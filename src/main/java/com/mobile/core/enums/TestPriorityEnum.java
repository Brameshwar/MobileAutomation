package com.mobile.core.enums;

import lombok.Getter;

@Getter
public enum TestPriorityEnum {

    CRITICAL("CRITICAL"),
    P1("P1"),
    P2("P2"),
    P3("P3"),
    P4("P4");

    private final String priority;

    TestPriorityEnum(String priority) {
        this.priority = priority;
    }

}
