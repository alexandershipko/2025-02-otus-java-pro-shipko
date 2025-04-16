package ru.otus.atm.domain;

public interface Cell {

    Denomination getDenomination();

    int getCount();

    int getMaxCount();

    void deposit(int count);

    void withdraw(int count);

    int getBalance();

}