package ru.otus.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.Processor;
import ru.otus.processor.homework.DateTimeProvider;
import ru.otus.processor.homework.ProcessorThrowOnEvenSecondWithMemento;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatCode;


class ProcessorThrowOnEvenSecondWithMementoTest {

    @Test
    @DisplayName("Тестируем процессор ProcessorThrowOnEvenSecondWithMemento (чётная секунда)")
    void shouldThrowExceptionOnEvenSecond() {
        // given
        DateTimeProvider mockProvider = () -> LocalDateTime.of(2024, 1, 1, 0, 0, 2);

        Processor processor = new ProcessorThrowOnEvenSecondWithMemento(mockProvider);
        Message message = new Message.Builder(1L).build();

        // when + then
        assertThatThrownBy(() -> processor.process(message))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("[EvenSecond]");
    }

    @Test
    @DisplayName("Тестируем процессор ProcessorThrowOnEvenSecondWithMemento (нечётная секунда)")
    void shouldNotThrowExceptionOnOddSecond() {
        // given
        DateTimeProvider dateTimeProvider = () -> LocalDateTime.of(2025, 1, 1, 12, 0, 3);
        Processor processor = new ProcessorThrowOnEvenSecondWithMemento(dateTimeProvider);
        Message message = new Message.Builder(1L).build();

        // when + then
        assertThatCode(() -> processor.process(message))
                .doesNotThrowAnyException();
    }

}