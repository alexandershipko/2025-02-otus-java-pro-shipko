package ru.otus.atm.domain;

public enum Denomination {

    HUNDRED(100),
    TWO_HUNDRED(200),
    FIVE_HUNDRED(500),
    THOUSAND(1_000),
    TWO_THOUSAND(2_000),
    FIVE_THOUSAND(5_000);

    private final int value;

    Denomination(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

}