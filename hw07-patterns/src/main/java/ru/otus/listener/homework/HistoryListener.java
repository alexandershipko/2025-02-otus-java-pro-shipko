package ru.otus.listener.homework;

import ru.otus.listener.Listener;
import ru.otus.model.Message;
import ru.otus.model.MessageMemento;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;


public class HistoryListener implements Listener, HistoryReader {

    private final Map<Long, MessageMemento> history = new HashMap<>();


    @Override
    public void onUpdated(Message msg) {
        history.put(msg.getId(), new MessageMemento(msg));
    }

    @Override
    public Optional<Message> findMessageById(long id) {
        return Optional.ofNullable(history.get(id))
                .map(MessageMemento::restore);
    }

}