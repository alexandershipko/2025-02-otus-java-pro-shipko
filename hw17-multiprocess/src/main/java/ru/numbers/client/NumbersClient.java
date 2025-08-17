package ru.numbers.client;

import io.grpc.ManagedChannelBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.numbers.AppProperties;
import ru.numbers.NumbersRequest;
import ru.numbers.NumbersServiceGrpc;

import java.util.concurrent.TimeUnit;


public class NumbersClient {
    private static final Logger log = LoggerFactory.getLogger(NumbersClient.class);

    private static final int FIRST_VALUE = 0;
    private static final int LAST_VALUE = 30;

    private static final int CLIENT_LOOP_SIZE = 50;

    private long value = 0;

    public static void main(String[] args) {
        log.info("Запуск NumbersClient");

        var managedChannel = ManagedChannelBuilder
                .forAddress(AppProperties.getServerHost(), AppProperties.getServerPort())
                .usePlaintext()
                .build();

        var asyncClient = NumbersServiceGrpc.newStub(managedChannel);

        ClientStreamObserver observer = new NumbersClient().clientAction(asyncClient);

        try {
            boolean completed = observer.awaitCompletion(30, TimeUnit.SECONDS);
            if (!completed) {
                log.warn("Таймаут ожидания завершения стрима");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        log.info("Завершаем работу NumbersClient");

        managedChannel.shutdown();

        try {
            if (!managedChannel.awaitTermination(5, TimeUnit.SECONDS)) {
                managedChannel.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            managedChannel.shutdownNow();
        }
    }

    private ClientStreamObserver clientAction(NumbersServiceGrpc.NumbersServiceStub asyncClient) {
        var numbersRequest = makeNumberRequest();
        var clientSteamObserver = new ClientStreamObserver();

        asyncClient.streamNumbers(numbersRequest, clientSteamObserver);

        for (var i = 0; i < CLIENT_LOOP_SIZE; i++) {
            var valForPrint = getNextValue(clientSteamObserver);
            log.info("текущее значение: {}, i: {}", valForPrint, i);
            sleep();
        }

        return clientSteamObserver;
    }

    private long getNextValue(ClientStreamObserver clientStreamObserver) {
        value = value + clientStreamObserver.getLastValueAndReset() + 1;
        return value;
    }

    private NumbersRequest makeNumberRequest() {
        return NumbersRequest.newBuilder()
                .setFirstValue(FIRST_VALUE)
                .setLastValue(LAST_VALUE)
                .build();
    }

    private static void sleep() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

}