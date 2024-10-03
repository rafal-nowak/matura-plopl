package pl.lodz.p.liceum.matura.domain;

import lombok.Getter;

@Getter
public enum TestType {

    FAST("FAST"),
    FULL("FULL");

    private final String value;

    TestType(String value) {
        this.value = value;
    }
}
