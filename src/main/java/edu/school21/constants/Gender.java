package edu.school21.constants;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE("male"),
    FEMALE("female");

    private final String value;

    Gender(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}