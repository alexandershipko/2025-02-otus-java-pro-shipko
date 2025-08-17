package ru.numbers.server;

import io.grpc.Server;
import io.grpc.ServerBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.numbers.AppProperties;

import java.io.IOException;

public class NumbersServer {
    private static final Logger log = LoggerFactory.getLogger(NumbersServer.class);

    public static void main(String[] args) throws IOException, InterruptedException {
        log.info("Запуск NumbersServer");

        int port = AppProperties.getServerPort();

        Server server =  ServerBuilder
                .forPort(port)
                .addService(new NumbersServiceImpl())
                .build();

        server.start();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("Получен сигнал на завершение");
            server.shutdown();
            try {
                if (!server.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    log.info("Принудительная остановка сервера");
                    server.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                server.shutdownNow();
            } finally {
                NumbersServiceImpl.shutdownScheduler();
            }

            log.info("Сервер остановлен");
        }));

        log.info("Сервер ожидает подключения клиентов, порт: {}", port);

        server.awaitTermination();
    }

}