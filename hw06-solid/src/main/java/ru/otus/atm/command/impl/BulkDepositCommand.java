package ru.otus.atm.command.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.atm.command.ATMCommand;
import ru.otus.atm.core.ATM;
import ru.otus.atm.domain.Denomination;

import java.util.Map;


public class BulkDepositCommand implements ATMCommand {

    private static final Logger logger = LoggerFactory.getLogger(BulkDepositCommand.class);

    private static final String DEPOSITED_BANKNOTES_OF = "Deposited {} banknotes of {}";

    private final Map<Denomination, Integer> deposits;


    public BulkDepositCommand(Map<Denomination, Integer> deposits) {
        this.deposits = deposits;
    }

    @Override
    public void execute(ATM atm) {
        for (Map.Entry<Denomination, Integer> entry : deposits.entrySet()) {
            Denomination denomination = entry.getKey();
            int count = entry.getValue();

            atm.deposit(denomination, count);
            logger.info(DEPOSITED_BANKNOTES_OF, count, denomination);
        }
    }

}