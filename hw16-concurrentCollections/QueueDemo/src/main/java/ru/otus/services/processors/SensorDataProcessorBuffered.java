package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;


@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);

    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final BlockingQueue<SensorData> queue;

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        if (bufferSize <= 0) {
            throw new IllegalArgumentException("bufferSize должен быть > 0");
        }

        this.bufferSize = bufferSize;
        this.writer = Objects.requireNonNull(writer, "writer не должен быть null");
        this.queue = new PriorityBlockingQueue<>(bufferSize,
                Comparator.comparing(SensorData::getMeasurementTime));
    }

    @Override
    public void process(SensorData data) {
        Objects.requireNonNull(data, "data не должен быть null");
        boolean isAdded = queue.offer(data);

        if(!isAdded) {
            log.error("Не удалось добавить элемент, буфер переполнен");
        }

        if (queue.size() >= bufferSize) {
            flush();
        }
    }

    public void flush() {
        try {
            var sensorDataList = new ArrayList<SensorData>();

            if (queue.drainTo(sensorDataList, bufferSize) > 0) {
                writer.writeBufferedData(sensorDataList);
            }
        } catch (Exception e) {
            log.error("Ошибка в процессе записи буфера", e);
        }
    }

    @Override
    public void onProcessingEnd() {
        flush();
    }

}
