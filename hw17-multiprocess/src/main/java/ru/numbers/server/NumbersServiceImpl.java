package ru.numbers.server;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.numbers.NumbersRequest;
import ru.numbers.NumbersResponse;
import ru.numbers.NumbersServiceGrpc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import io.grpc.stub.ServerCallStreamObserver;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;

public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(NumbersServiceImpl.class);

    private static final ScheduledThreadPoolExecutor EXECUTOR = createExecutor();
    private static final AtomicLong threadCounter = new AtomicLong(0);

    private static ScheduledThreadPoolExecutor createExecutor() {
        ThreadFactory tf = new ThreadFactory() {
            private final ThreadFactory delegate = Executors.defaultThreadFactory();
            @Override
            public Thread newThread(Runnable r) {
                Thread t = delegate.newThread(r);
                t.setDaemon(true);
                t.setName("numbers-scheduler-" + threadCounter.getAndIncrement());
                return t;
            }
        };
        ScheduledThreadPoolExecutor stpe = new ScheduledThreadPoolExecutor(1, tf);
        stpe.setRemoveOnCancelPolicy(true);
        return stpe;
    }

    public static void shutdownScheduler() {
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
        log.info("Запрошена новая последовательность чисел: firstValue:{}, lastValue:{}",
                request.getFirstValue(), request.getLastValue());

        if (request.getFirstValue() >= request.getLastValue()) {
            log.warn("Некорректный диапазон: firstValue ({}) >= lastValue ({})",
                    request.getFirstValue(), request.getLastValue());
            responseObserver.onError(new IllegalArgumentException("firstValue должно быть меньше lastValue"));

            return;
        }

        var serverObserver = (ServerCallStreamObserver<NumbersResponse>) responseObserver;
        var currentValue = new AtomicLong(request.getFirstValue());
        var done = new AtomicBoolean(false);

        final ScheduledFuture<?>[] futureHolder = new ScheduledFuture<?>[1];

        Runnable stop = () -> {
            if (done.compareAndSet(false, true)) {
                ScheduledFuture<?> f = futureHolder[0];
                if (f != null && !f.isCancelled()) {
                    f.cancel(false);
                }
            }
        };

        serverObserver.setOnCancelHandler(() -> {
            log.info("Клиент отменил запрос, останавливаем отправку");
            stop.run();
        });

        Runnable task = () -> {
            try {
                if (done.get() || serverObserver.isCancelled()) {
                    stop.run();
                    return;
                }

                long next = currentValue.incrementAndGet();
                if (next > request.getLastValue()) {
                    stop.run();
                    return;
                }

                try {
                    serverObserver.onNext(NumbersResponse.newBuilder().setNumber(next).build());
                } catch (StatusRuntimeException sre) {
                    if (sre.getStatus().getCode() == Status.Code.CANCELLED
                            || sre.getStatus().getCode() == Status.Code.UNAVAILABLE) {
                        stop.run();
                        return;
                    }
                    throw sre;
                }

                if (next == request.getLastValue()) {
                    stop.run();
                    if (!serverObserver.isCancelled()) {
                        try {
                            serverObserver.onCompleted();
                            log.info("Последовательность чисел завершена");
                        } catch (StatusRuntimeException sre) {
                            if (sre.getStatus().getCode() != Status.Code.CANCELLED
                                    && sre.getStatus().getCode() != Status.Code.UNAVAILABLE) {
                                throw sre;
                            }
                        }
                    }
                }
            } catch (StatusRuntimeException e) {
                var code = e.getStatus().getCode();
                if (code == Status.Code.CANCELLED || code == Status.Code.UNAVAILABLE) {
                    stop.run();
                } else {
                    try {
                        serverObserver.onError(e);
                    } catch (Exception ignore) {
                    } finally {
                        stop.run();
                    }
                }
            } catch (Exception e) {
                try {
                    serverObserver.onError(e);
                } catch (Exception ignore) {
                } finally {
                    stop.run();
                }
            }
        };

        try {
            futureHolder[0] = EXECUTOR.scheduleAtFixedRate(task, 2, 2, TimeUnit.SECONDS);
        } catch (RejectedExecutionException ex) {
            if (!serverObserver.isCancelled()) {
                try {
                    serverObserver.onError(Status.UNAVAILABLE
                            .withDescription("Остановка сервера")
                            .asRuntimeException());
                } catch (Exception ignore) {}
            }
        }
    }

}