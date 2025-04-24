package ru.otus.model;

public class MessageMemento {

    private final Message snapshot;


    public MessageMemento(Message message) {
        this.snapshot = message.toBuilder().build();
    }

    public Message restore() {
        return snapshot;
    }

}