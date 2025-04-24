package ru.otus.processor.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.exception.EvenSecondException;
import ru.otus.model.Message;
import ru.otus.model.MessageMemento;
import ru.otus.processor.Processor;


@SuppressWarnings({"java:S2139"})
public class ProcessorThrowOnEvenSecondWithMemento implements Processor {

    private static final Logger logger = LoggerFactory.getLogger(ProcessorThrowOnEvenSecondWithMemento.class);

    private final DateTimeProvider dateTimeProvider;


    public ProcessorThrowOnEvenSecondWithMemento(DateTimeProvider dateTimeProvider) {
        this.dateTimeProvider = dateTimeProvider;
    }

    @Override
    public Message process(Message message) {
        MessageMemento memento = new MessageMemento(message);

        Message newMessage;
        try {
            int second = dateTimeProvider.getDateTime().getSecond();
            if (second % 2 == 0) {
                throw new EvenSecondException("[EvenSecond] - Exception");
            }

            newMessage = message.toBuilder()
                    .field10(message.getField10() + " [ok]")
                    .build();

        } catch (Exception ex) {
            newMessage = memento.restore();

            logger.error("Exception during processing: {}", ex.getMessage());
            logger.info("Restored message: {}", newMessage);

            throw ex;
        }

        return newMessage;
    }

}