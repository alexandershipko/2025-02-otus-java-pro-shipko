package ru.otus.atm.domain.impl;

import ru.otus.atm.domain.Cell;
import ru.otus.atm.domain.CellGroup;
import ru.otus.atm.domain.Denomination;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;


public class WithdrawalCellGroup implements CellGroup {

    private final Map<Denomination, Cell> cells = new EnumMap<>(Denomination.class);


    @Override
    public void addCell(Cell cell) {
        cells.put(cell.getDenomination(), cell);
    }

    @Override
    public Map<Denomination, Cell> getCells() {
        return Collections.unmodifiableMap(cells);
    }

    @Override
    public int getBalance() {
        return cells.values().stream().mapToInt(Cell::getBalance).sum();
    }

}