package ru.otus.atm.strategy.impl;

import ru.otus.atm.domain.Cell;
import ru.otus.atm.domain.Denomination;
import ru.otus.atm.exception.InsufficientFundsException;
import ru.otus.atm.strategy.WithdrawalStrategy;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


public class MinimalNotesStrategy implements WithdrawalStrategy {

    @Override
    public Map<Denomination, Integer> withdraw(int amount, Map<Denomination, Cell> cells) {
        List<Denomination> sorted = new ArrayList<>(cells.keySet());
        sorted.sort((a, b) -> Integer.compare(b.getValue(), a.getValue()));

        Map<Denomination, Integer> result = new LinkedHashMap<>();
        int remaining = amount;

        for (Denomination denomination : sorted) {
            Cell cell = cells.get(denomination);
            int available = cell.getCount();
            int needed = remaining / denomination.getValue();
            int toUse = Math.min(needed, available);
            if (toUse > 0) {
                result.put(denomination, toUse);
                remaining -= toUse * denomination.getValue();
            }
        }

        if (remaining > 0) {
            throw new InsufficientFundsException("Cannot dispense amount: " + amount);
        }

        result.forEach((d, count) -> cells.get(d).withdraw(count));
        return result;
    }

}