package ru.otus.demo;

import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DbServiceClientImpl;

import java.util.ArrayList;
import java.util.List;

//-Xms64m -Xmx64m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=./logs/heapdump.hprof -XX:+UseG1GC -Xlog:gc=debug:file=./logs/gc-%p-%t.log:tags,uptime,time,level:filecount=5,filesize=10m

public class PerformanceTest {

    private static final Logger logger = LoggerFactory.getLogger(PerformanceTest.class);

    private static final String HIBERNATE_CFG_FILE = "hibernate.cfg.xml";

    private TransactionManagerHibernate transactionManager;

    private DbServiceClientImpl dbServiceClient;


    public PerformanceTest() {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);
        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class);
        transactionManager = new TransactionManagerHibernate(sessionFactory);
        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);
    }

    public void testPerformance(int numberOfClients) {
        List<Long> clientIds = new ArrayList<>();

        //write to DB
        long startInsertTime = System.currentTimeMillis();
        for (int i = 1; i <= numberOfClients; i++) {
            var address = new Address("Street " + i);
            var phone1 = new Phone("123-456-78" + i);
            var phone2 = new Phone("098-765-43" + i);
            var client = new Client(null, "Client " + i, address, List.of(phone1, phone2));
            Client savedClient = dbServiceClient.saveClient(client);
            clientIds.add(savedClient.getId());
        }
        long endInsertTime = System.currentTimeMillis();
        logger.info("Time taken to insert {} clients: {} ms", numberOfClients, (endInsertTime - startInsertTime));

        //read from DB or from cache
        long startReadTime = System.currentTimeMillis();
        for (Long clientId : clientIds) {
            dbServiceClient.getClient(clientId)
                    .orElseThrow(() -> new RuntimeException("Client not found, id:" + clientId));
        }
        long endReadTime = System.currentTimeMillis();
        logger.info("Time taken to read {} clients: {} ms", numberOfClients, (endReadTime - startReadTime));
    }

    public static void main(String[] args) {
        PerformanceTest performanceTest = new PerformanceTest();
        int numberOfClients = 1_000;

        performanceTest.testPerformance(numberOfClients);
    }

}
