package ru.otus.atm.domain;

import java.util.Map;


public interface CellGroup {

    void addCell(Cell cell);

    Map<Denomination, Cell> getCells();

    int getBalance();

}