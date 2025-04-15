package ru.otus.atm.core;

import ru.otus.atm.domain.Cell;
import ru.otus.atm.domain.Denomination;
import ru.otus.atm.domain.impl.CellImpl;
import ru.otus.atm.domain.impl.DepositCellGroup;
import ru.otus.atm.domain.impl.WithdrawalCellGroup;
import ru.otus.atm.strategy.WithdrawalStrategy;

import java.util.Map;


public class ATM {

    public static final int MAX_COUNT = 2_000;

    private final DepositCellGroup depositGroup;

    private final WithdrawalCellGroup withdrawalGroup;

    private final WithdrawalStrategy withdrawalStrategy;


    public ATM(WithdrawalStrategy withdrawalStrategy) {
        this.withdrawalStrategy = withdrawalStrategy;
        this.depositGroup = new DepositCellGroup();
        this.withdrawalGroup = new WithdrawalCellGroup();
    }

    // Метод для депозита
    public void deposit(Denomination denomination, int count) {
        Cell cell = depositGroup.getCells().get(denomination);
        if (cell == null) {
            cell = new CellImpl(denomination, MAX_COUNT);
            depositGroup.addCell(cell);
        }
        cell.deposit(count);
    }

    // Метод для снятия средств
    public Map<Denomination, Integer> withdraw(int amount) {
        return withdrawalStrategy.withdraw(amount, withdrawalGroup.getCells());
    }

    // Метод для перевода средств из депозитных ячеек в ячейки для выдачи
    public void transferDepositsToWithdrawals() {
        for (Map.Entry<Denomination, Cell> entry : depositGroup.getCells().entrySet()) {
            Denomination denomination = entry.getKey();
            Cell depositCell = entry.getValue();

            Cell withdrawCell = withdrawalGroup.getCells().get(denomination);
            if (withdrawCell == null) {
                withdrawCell = new CellImpl(denomination, MAX_COUNT);
                withdrawalGroup.addCell(withdrawCell);
            }

            int countToTransfer = depositCell.getCount();
            depositCell.withdraw(countToTransfer);
            withdrawCell.deposit(countToTransfer);
        }
    }

    // Получение всех ячеек депозита
    public Map<Denomination, Cell> getDepositCells() {
        return depositGroup.getCells();
    }

    // Получение всех ячеек для выдачи
    public Map<Denomination, Cell> getWithdrawalCells() {
        return withdrawalGroup.getCells();
    }

    // Получение суммы на депозитных ячейках
    public int getDepositedAmount() {
        return depositGroup.getBalance();
    }

    // Получение доступной суммы для снятия
    public int getAvailableAmountForWithdrawal() {
        return withdrawalGroup.getBalance();
    }

    // Получение общего баланса
    public int getTotalBalance() {
        return getDepositedAmount() + getAvailableAmountForWithdrawal();
    }

}