package ru.otus.handler;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import ru.otus.model.Message;
import ru.otus.processor.Processor;
import ru.otus.processor.homework.ProcessorSwapField11And12;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


class ProcessorSwapField11And12Test {

    @Test
    @DisplayName("Тестируем процессор ProcessorSwapField11And12")
    void swapField11And12Test() {
        // given
        var originalMessage = new Message.Builder(1L)
                .field11("value11")
                .field12("value12")
                .build();

        List<Processor> processors = List.of(new ProcessorSwapField11And12());

        var complexProcessor = new ComplexProcessor(processors, (ex) -> {
        });

        // when
        var result = complexProcessor.handle(originalMessage);

        // then
        assertThat(result.getField11()).isEqualTo("value12");
        assertThat(result.getField12()).isEqualTo("value11");
        assertThat(result.getField11()).isNotEqualTo(originalMessage.getField11());
        assertThat(result.getField12()).isNotEqualTo(originalMessage.getField12());
    }

}