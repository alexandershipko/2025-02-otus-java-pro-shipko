package ru.otus.atm.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.atm.command.ATMCommand;
import ru.otus.atm.core.ATM;
import ru.otus.atm.domain.Denomination;

import java.util.Map;


public class WithdrawCommand implements ATMCommand {

    private static final Logger logger = LoggerFactory.getLogger(WithdrawCommand.class);

    private final int amount;


    public WithdrawCommand(int amount) {
        this.amount = amount;
    }

    @Override
    public void execute(ATM atm) {
        Map<Denomination, Integer> result = atm.withdraw(amount);

        logger.info("\nWithdrawal request for {} processed:", amount);

        result.forEach((denomination, count) ->
                logger.info("{} x {}", denomination, count)
        );
    }

}