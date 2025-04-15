package ru.otus.atm.domain.impl;

import ru.otus.atm.domain.Cell;
import ru.otus.atm.domain.Denomination;
import ru.otus.atm.exception.InsufficientFundsException;
import ru.otus.atm.exception.InvalidDepositException;


public class CellImpl implements Cell {

    private final Denomination denomination;

    private final int maxCount;

    private int count;


    public CellImpl(Denomination denomination, int maxCount) {
        this.denomination = denomination;
        this.maxCount = maxCount;
        this.count = 0;
    }

    @Override
    public Denomination getDenomination() {
        return denomination;
    }

    @Override
    public int getCount() {
        return count;
    }

    @Override
    public int getMaxCount() {
        return maxCount;
    }

    @Override
    public void deposit(int count) {
        if (this.count + count > maxCount) {
            throw new InvalidDepositException("Max capacity exceeded for " + denomination);
        }
        this.count += count;
    }

    @Override
    public void withdraw(int count) {
        if (this.count < count) {
            throw new InsufficientFundsException("Not enough notes of " + denomination);
        }
        this.count -= count;
    }

    @Override
    public int getBalance() {
        return denomination.getValue() * count;
    }

}