package ru.otus.atm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.atm.command.ATMCommand;
import ru.otus.atm.command.impl.BulkDepositCommand;
import ru.otus.atm.command.impl.WithdrawCommand;
import ru.otus.atm.core.ATM;
import ru.otus.atm.domain.Denomination;
import ru.otus.atm.strategy.impl.MinimalNotesStrategy;

import java.util.Map;


public class ATMApplication {

    private static final Logger logger = LoggerFactory.getLogger(ATMApplication.class);


    //test
    public static void main(String[] args) {
        logger.info("\nATM Application started.");

        ATM atm = new ATM(new MinimalNotesStrategy());
        logger.info("\nATM created with MinimalNotesStrategy.");


        // Выполняем депозит средств (ячейки для выдачи)
        Map<Denomination, Integer> deposits = Map.of(
                Denomination.HUNDRED, 2_000,
                Denomination.TWO_HUNDRED, 2,
                Denomination.FIVE_HUNDRED, 3,
                Denomination.THOUSAND, 4,
                Denomination.TWO_THOUSAND, 5,
                Denomination.FIVE_THOUSAND, 6
        );

        ATMCommand depositCommand = new BulkDepositCommand(deposits);
        depositCommand.execute(atm);


        // Перемещаем средства из депозитных ячеек в ячейки для выдачи
        atm.transferDepositsToWithdrawals();
        logger.info("\nTransferred funds from deposit cells to withdrawal cells.");


        // Выводим текущий баланс банкомата
        int totalBalance = atm.getTotalBalance();
        logger.info("TotalBalance: {}", totalBalance);

        int depositedAmount = atm.getDepositedAmount();
        logger.info("DepositedAmount: {}", depositedAmount);

        int availableAmountForWithdrawal = atm.getAvailableAmountForWithdrawal();
        logger.info("AvailableAmountForWithdrawal: {}", availableAmountForWithdrawal);


        // Выполняем операцию снятия средств
        ATMCommand withdrawCommand = new WithdrawCommand(59500);
        withdrawCommand.execute(atm);


        // Пополняем (клиент)
        deposits = Map.of(
                Denomination.TWO_HUNDRED, 7,
                Denomination.TWO_THOUSAND, 2_000 // ошибка при 2_001
        );

        depositCommand = new BulkDepositCommand(deposits);
        depositCommand.execute(atm);


        // Выводим баланс банкомата
        totalBalance = atm.getTotalBalance();
        logger.info("\nTotalBalance: {}", totalBalance);


        depositedAmount = atm.getDepositedAmount();
        logger.info("\nDepositedAmount: {}", depositedAmount);
        atm.getDepositCells().forEach((denomination, cell) -> {
            String message = cell.getCount() > 0
                    ? String.format("%s x %d", denomination, cell.getCount())
                    : String.format("%s (empty)", denomination);
            logger.info(message);
        });


        availableAmountForWithdrawal = atm.getAvailableAmountForWithdrawal();
        logger.info("\nAvailableAmountForWithdrawal: {}", availableAmountForWithdrawal);
        atm.getWithdrawalCells().forEach((denomination, cell) -> {
            String message = cell.getCount() > 0
                    ? String.format("%s x %d", denomination, cell.getCount())
                    : String.format("%s (empty)", denomination);
            logger.info(message);
        });


        logger.info("\nATM Application finished.");
    }

}