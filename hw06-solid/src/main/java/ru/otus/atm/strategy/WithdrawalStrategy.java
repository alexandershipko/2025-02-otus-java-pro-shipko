package ru.otus.atm.strategy;

import ru.otus.atm.domain.Cell;
import ru.otus.atm.domain.Denomination;

import java.util.Map;


public interface WithdrawalStrategy {

    Map<Denomination, Integer> withdraw(int amount, Map<Denomination, Cell> cells);

}