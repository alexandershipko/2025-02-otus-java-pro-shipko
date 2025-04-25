package ru.otus.exception;

public class EvenSecondException extends RuntimeException {

    public EvenSecondException(String message) {
        super(message);
    }

    public EvenSecondException(String message, Throwable cause) {
        super(message, cause);
    }

}