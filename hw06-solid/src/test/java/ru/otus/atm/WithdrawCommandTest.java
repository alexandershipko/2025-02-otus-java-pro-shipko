package ru.otus.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.command.ATMCommand;
import ru.otus.atm.command.impl.WithdrawCommand;
import ru.otus.atm.core.ATM;
import ru.otus.atm.domain.Denomination;

import java.util.EnumMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class WithdrawCommandTest {

    private ATM atmMock;


    @BeforeEach
    void setUp() {
        atmMock = mock(ATM.class);
    }

    @Test
    @DisplayName("testExecuteShouldCallWithdrawAndLogResult")
    void testExecuteShouldCallWithdrawAndLogResult() {
        int amount = 1000;

        Map<Denomination, Integer> expectedWithdrawal = new EnumMap<>(Denomination.class);
        expectedWithdrawal.put(Denomination.FIVE_HUNDRED, 2);

        when(atmMock.withdraw(amount)).thenReturn(expectedWithdrawal);

        ATMCommand command = new WithdrawCommand(amount);
        command.execute(atmMock);

        verify(atmMock, times(1)).withdraw(amount);
        verifyNoMoreInteractions(atmMock);
    }

    @Test
    @DisplayName("testExecuteShouldThrowWhenATMThrows")
    void testExecuteShouldThrowWhenATMThrows() {
        int amount = 777;

        when(atmMock.withdraw(amount)).thenThrow(new IllegalArgumentException("Invalid request"));

        ATMCommand command = new WithdrawCommand(amount);

        assertThrows(IllegalArgumentException.class, () -> {
            command.execute(atmMock);
        });

        verify(atmMock, times(1)).withdraw(amount);
    }

}