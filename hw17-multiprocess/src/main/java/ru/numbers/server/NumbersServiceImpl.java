package ru.numbers.server;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.numbers.NumbersRequest;
import ru.numbers.NumbersResponse;
import ru.numbers.NumbersServiceGrpc;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;
import io.grpc.stub.ServerCallStreamObserver;

import java.util.concurrent.atomic.AtomicBoolean;

public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(NumbersServiceImpl.class);
    private static final ScheduledExecutorService EXECUTOR = Executors.newSingleThreadScheduledExecutor(
            r -> {
                Thread t = new Thread(r, "numbers-scheduler");
                t.setDaemon(true);
                return t;
            }
    );

    public static void shutdown() {
        EXECUTOR.shutdown();
        try {
            if (!EXECUTOR.awaitTermination(5, TimeUnit.SECONDS)) {
                EXECUTOR.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            EXECUTOR.shutdownNow();
        }
    }

    @Override
    public void streamNumbers(NumbersRequest request, StreamObserver<NumbersResponse> responseObserver) {
        if (request.getFirstValue() >= request.getLastValue()) {
            responseObserver.onError(new IllegalArgumentException("firstValue должно быть меньше lastValue"));

            return;
        }

        ServerCallStreamObserver<NumbersResponse> serverObserver = (ServerCallStreamObserver<NumbersResponse>) responseObserver;
        AtomicLong currentValue = new AtomicLong(request.getFirstValue());
        AtomicBoolean isActive = new AtomicBoolean(true);

        Runnable task = new NumberGeneratorTask(serverObserver, currentValue, isActive, request.getLastValue());
        ScheduledFuture<?> future = EXECUTOR.scheduleAtFixedRate(task, 2, 2, TimeUnit.SECONDS);

        serverObserver.setOnCancelHandler(() -> {
            log.info("Клиент отменил запрос, останавливаем отправку");

            isActive.set(false);
            future.cancel(false);
        });
    }

    private static class NumberGeneratorTask implements Runnable {
        private final ServerCallStreamObserver<NumbersResponse> observer;
        private final AtomicLong currentValue;
        private final AtomicBoolean isActive;
        private final long lastValue;

        NumberGeneratorTask(ServerCallStreamObserver<NumbersResponse> observer,
                            AtomicLong currentValue,
                            AtomicBoolean isActive,
                            long lastValue) {
            this.observer = observer;
            this.currentValue = currentValue;
            this.isActive = isActive;
            this.lastValue = lastValue;
        }

        @Override
        public void run() {
            if (!isActive.get() || observer.isCancelled()) {
                return;
            }

            long next = currentValue.incrementAndGet();
            log.info("Отправка числа: {}", next);

            if (next > lastValue) {
                isActive.set(false);
                complete();

                return;
            }

            try {
                observer.onNext(NumbersResponse.newBuilder().setNumber(next).build());

                if (next == lastValue) {
                    isActive.set(false);
                    complete();
                }
            } catch (StatusRuntimeException e) {
                if (!isCancellation(e)) {
                    handleError(e);
                }

                isActive.set(false);
            } catch (Exception e) {
                handleError(e);
                isActive.set(false);
            }
        }

        private void complete() {
            if (!observer.isCancelled()) {
                observer.onCompleted();
                log.info("Последовательность чисел завершена");
            }
        }

        private boolean isCancellation(StatusRuntimeException e) {
            Status.Code code = e.getStatus().getCode();

            return code == Status.Code.CANCELLED || code == Status.Code.UNAVAILABLE;
        }

        private void handleError(Throwable t) {
            try {
                if (!observer.isCancelled()) {
                    observer.onError(t);
                }
            } catch (Exception e) {
                log.error("Произошла ошибка: {}", e.getMessage());
            }
        }
    }

}
