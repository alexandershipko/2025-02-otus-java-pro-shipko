package ru.otus.atm.command;

import ru.otus.atm.core.ATM;


public interface ATMCommand {

    void execute(ATM atm);

}