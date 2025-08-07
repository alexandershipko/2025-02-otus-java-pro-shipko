package ru.otus.monitor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.NumberGenerator;

import java.util.ArrayList;
import java.util.List;


public class MonitorNumber {
    private static final Logger logger = LoggerFactory.getLogger(MonitorNumber.class);

    private static final String THREAD_1 = "Thread-1";
    private static final String THREAD_2 = "Thread-2";

    private final Object lock = new Object();
    private String currentTurn = THREAD_1;


    public List<Thread> alternateNumbers() {
        NumberGenerator generator1 = new NumberGenerator();
        NumberGenerator generator2 = new NumberGenerator();

        List<Thread> threads = new ArrayList<>();
        threads.add(startThread(THREAD_1, THREAD_2, generator1));
        threads.add(startThread(THREAD_2, THREAD_1, generator2));

        return threads;
    }

    private Thread startThread(String threadName, String nextThread, NumberGenerator generator) {
        Thread thread = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                action(threadName, nextThread, generator);
            }
        }, threadName);
        thread.start();
        return thread;
    }

    private void action(String currentThread, String nextThread, NumberGenerator generator) {
        synchronized (lock) {
            try {
                while (!currentThread.equals(currentTurn)) {
                    lock.wait();
                }

                int number = generator.getAndUpdate();
                logger.info("{}: {}", currentThread, number);

                currentTurn = nextThread;
                sleep();

                lock.notifyAll();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            logger.error(e.getMessage());
            Thread.currentThread().interrupt();
        }
    }

}