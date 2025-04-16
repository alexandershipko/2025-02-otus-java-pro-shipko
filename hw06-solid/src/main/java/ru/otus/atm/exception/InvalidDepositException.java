package ru.otus.atm.exception;

public class InvalidDepositException extends RuntimeException {

    public InvalidDepositException(String message) {
        super(message);
    }

    public InvalidDepositException(String message, Throwable cause) {
        super(message, cause);
    }

}