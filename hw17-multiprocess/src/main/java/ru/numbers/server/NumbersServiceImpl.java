package ru.numbers.server;

import io.grpc.stub.StreamObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.numbers.NumbersRequest;
import ru.numbers.NumbersResponse;
import ru.numbers.NumbersServiceGrpc;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;


public class NumbersServiceImpl extends NumbersServiceGrpc.NumbersServiceImplBase {
    private static final Logger log = LoggerFactory.getLogger(NumbersServiceImpl.class);

    private static final ScheduledExecutorService EXECUTOR = Executors.newScheduledThreadPool(1);

    @Override
    public void streamNumbers(NumbersRequest request, StreamObserver<NumbersResponse> responseObserver) {
        log.info("Запрошена новая последовательность чисел: firstValue:{}, lastValue:{}",
                request.getFirstValue(), request.getLastValue());

        var currentValue = new AtomicLong(request.getFirstValue());

        final ScheduledFuture<?>[] futureHolder = new ScheduledFuture<?>[1];

        Runnable task = () -> {
            long value = currentValue.incrementAndGet();
            var response = NumbersResponse.newBuilder().setNumber(value).build();
            responseObserver.onNext(response);

            if (value >= request.getLastValue()) {
                responseObserver.onCompleted();
                log.info("Последовательность чисел завершена");
                futureHolder[0].cancel(false);
            }
        };

        futureHolder[0] = EXECUTOR.scheduleAtFixedRate(task, 0, 2, TimeUnit.SECONDS);
    }

}
