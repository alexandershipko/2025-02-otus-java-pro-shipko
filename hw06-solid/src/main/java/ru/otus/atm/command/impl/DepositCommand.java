package ru.otus.atm.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.atm.command.ATMCommand;
import ru.otus.atm.core.ATM;
import ru.otus.atm.domain.Denomination;


public class DepositCommand implements ATMCommand {

    private static final Logger logger = LoggerFactory.getLogger(DepositCommand.class);

    private final Denomination denomination;

    private final int count;


    public DepositCommand(Denomination denomination, int count) {
        this.denomination = denomination;
        this.count = count;
    }

    @Override
    public void execute(ATM atm) {
        atm.deposit(denomination, count);
        logger.info("\nDeposited {} x {}", count, denomination);
    }

}