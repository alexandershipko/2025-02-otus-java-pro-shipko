package ru.otus.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.command.ATMCommand;
import ru.otus.atm.command.impl.BulkDepositCommand;
import ru.otus.atm.core.ATM;
import ru.otus.atm.domain.Denomination;

import java.util.EnumMap;
import java.util.Map;

import static org.mockito.Mockito.*;


class BulkDepositCommandTest {

    private ATM atmMock;


    @BeforeEach
    void setUp() {
        atmMock = mock(ATM.class);
    }

    @Test
    @DisplayName("testExecuteShouldCallDepositForEachDenomination")
    void testExecuteShouldCallDepositForEachDenomination() {
        Map<Denomination, Integer> deposits = new EnumMap<>(Denomination.class);
        deposits.put(Denomination.TWO_HUNDRED, 7);
        deposits.put(Denomination.TWO_THOUSAND, 2000);

        ATMCommand command = new BulkDepositCommand(deposits);
        command.execute(atmMock);

        verify(atmMock, times(1)).deposit(Denomination.TWO_HUNDRED, 7);
        verify(atmMock, times(1)).deposit(Denomination.TWO_THOUSAND, 2000);
        verifyNoMoreInteractions(atmMock);
    }

    @Test
    @DisplayName("testExecuteShouldHandleEmptyDepositMap")
    void testExecuteShouldHandleEmptyDepositMap() {
        Map<Denomination, Integer> deposits = new EnumMap<>(Denomination.class);
        ATMCommand command = new BulkDepositCommand(deposits);
        command.execute(atmMock);

        verifyNoInteractions(atmMock);
    }

}