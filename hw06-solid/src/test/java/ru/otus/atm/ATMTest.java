package ru.otus.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.core.ATM;
import ru.otus.atm.domain.Denomination;
import ru.otus.atm.strategy.impl.MinimalNotesStrategy;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


class ATMTest {

    private ATM atm;


    @BeforeEach
    void setUp() {
        atm = new ATM(new MinimalNotesStrategy());
    }

    @Test
    @DisplayName("testDeposit")
    void testDeposit() {
        Denomination denomination = Denomination.HUNDRED;
        int count = 100;

        atm.deposit(denomination, count);

        assertEquals(count * denomination.getValue(), atm.getDepositedAmount());
    }

    @Test
    @DisplayName("testTransferDepositsToWithdrawals")
    void testTransferDepositsToWithdrawals() {
        Denomination denomination = Denomination.HUNDRED;
        int depositCount = 100;

        atm.deposit(denomination, depositCount);
        atm.transferDepositsToWithdrawals();

        assertEquals(0, atm.getDepositedAmount());
        assertEquals(depositCount * denomination.getValue(), atm.getAvailableAmountForWithdrawal());
    }

    @Test
    @DisplayName("testWithdraw")
    void testWithdraw() {
        Denomination denomination = Denomination.HUNDRED;
        int depositCount = 100;

        atm.deposit(denomination, depositCount);
        atm.transferDepositsToWithdrawals();

        int withdrawalAmount = 5000;

        Map<Denomination, Integer> withdrawalResult = atm.withdraw(withdrawalAmount);

        assertEquals(50, withdrawalResult.get(denomination));
        assertEquals((depositCount - 50) * denomination.getValue(), atm.getAvailableAmountForWithdrawal());
    }

    @Test
    @DisplayName("testGetDepositCells")
    void testGetDepositCells() {
        Denomination denomination = Denomination.HUNDRED;
        int count = 100;

        atm.deposit(denomination, count);

        assertTrue(atm.getDepositCells().containsKey(denomination));
        assertEquals(count * denomination.getValue(), atm.getDepositCells().get(denomination).getBalance());
    }

    @Test
    @DisplayName("testGetWithdrawalCells")
    void testGetWithdrawalCells() {
        Denomination denomination = Denomination.HUNDRED;
        int count = 100;

        atm.deposit(denomination, count);
        atm.transferDepositsToWithdrawals();

        assertTrue(atm.getWithdrawalCells().containsKey(denomination));
        assertEquals(count * denomination.getValue(), atm.getWithdrawalCells().get(denomination).getBalance());
    }

    @Test
    @DisplayName("testTotalBalance")
    void testTotalBalance() {
        atm.deposit(Denomination.HUNDRED, 10);
        atm.deposit(Denomination.FIVE_HUNDRED, 10);

        assertEquals(6000, atm.getTotalBalance());

        atm.transferDepositsToWithdrawals();

        assertEquals(0, atm.getDepositedAmount());
        assertEquals(6000, atm.getAvailableAmountForWithdrawal());
        assertEquals(6000, atm.getTotalBalance());
    }

}