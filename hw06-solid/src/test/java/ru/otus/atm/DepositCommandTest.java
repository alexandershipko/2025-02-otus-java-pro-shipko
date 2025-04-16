package ru.otus.atm;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.atm.command.ATMCommand;
import ru.otus.atm.command.impl.DepositCommand;
import ru.otus.atm.core.ATM;
import ru.otus.atm.domain.Denomination;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


class DepositCommandTest {

    private ATM atmMock;


    @BeforeEach
    void setUp() {
        atmMock = mock(ATM.class);
    }

    @Test
    @DisplayName("testExecuteShouldCallDeposit")
    void testExecuteShouldCallDeposit() {
        Denomination denomination = Denomination.THOUSAND;
        int count = 5;

        ATMCommand command = new DepositCommand(denomination, count);
        command.execute(atmMock);

        verify(atmMock, times(1)).deposit(denomination, count);
        verifyNoMoreInteractions(atmMock);
    }

    @Test
    @DisplayName("testExecuteShouldThrowWhenATMThrows")
    void testExecuteShouldThrowWhenATMThrows() {
        Denomination denomination = Denomination.THOUSAND;
        int count = 5;

        doThrow(new IllegalArgumentException("Invalid deposit"))
                .when(atmMock).deposit(denomination, count);

        ATMCommand command = new DepositCommand(denomination, count);

        assertThrows(IllegalArgumentException.class, () -> {
            command.execute(atmMock);
        });

        verify(atmMock).deposit(denomination, count);
    }

}