package ru.otus.model;

import java.util.ArrayList;


public class MessageMemento {

    private final Message snapshot;


    public MessageMemento(Message message) {
        this.snapshot = deepCopy(message);
    }

    private Message deepCopy(Message message) {
        ObjectForMessage field13 = message.getField13();
        ObjectForMessage copiedField13 = null;

        if (field13 != null) {
            copiedField13 = new ObjectForMessage();
            copiedField13.setData(new ArrayList<>(field13.getData()));
        }

        return message.toBuilder()
                .field13(copiedField13)
                .build();
    }

    public Message restore() {
        return snapshot;
    }

}