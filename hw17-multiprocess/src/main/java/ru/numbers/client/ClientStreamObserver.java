package ru.numbers.client;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.numbers.NumbersResponse;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class ClientStreamObserver implements StreamObserver<NumbersResponse> {
    private static final Logger log = LoggerFactory.getLogger(ClientStreamObserver.class);

    private final ReentrantLock lock = new ReentrantLock();
    private final CountDownLatch done = new CountDownLatch(1);

    private long lastValue = 0;

    @Override
    public void onNext(NumbersResponse value) {
        log.info("Новое значение: {}", value.getNumber());

        setLastValue(value.getNumber());
    }

    @Override
    public void onError(Throwable t) {
        log.error("Произошла ошибка при получении значений", t);

        done.countDown();
    }

    @Override
    public void onCompleted() {
        log.info("Запрос завершен");

        done.countDown();
    }

    private void setLastValue(long value) {
        lock.lock();
        try {
            this.lastValue = value;
        } finally {
            lock.unlock();
        }
    }

    public long getLastValueAndReset() {
        lock.lock();
        try {
            var lastValuePrev = this.lastValue;
            this.lastValue = 0;

            return lastValuePrev;
        } finally {
            lock.unlock();
        }
    }

    public boolean awaitCompletion(long timeout, TimeUnit unit) throws InterruptedException {
        return done.await(timeout, unit);
    }

}