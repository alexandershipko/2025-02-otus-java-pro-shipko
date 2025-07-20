package ru.otus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceAddress;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DBServicePhone;

import java.util.HashSet;


@Component("actionDemo")
public class ActionDemo implements CommandLineRunner {
    private static final Logger log = LoggerFactory.getLogger(ActionDemo.class);

    private final DBServiceClient dbServiceClient;
    private final DBServiceAddress dbServiceAddress;
    private final DBServicePhone dBServicePhone;

    public ActionDemo(
            DBServiceClient dbServiceClient,
            DBServiceAddress dbServiceAddress,
            DBServicePhone dBServicePhone) {
        this.dbServiceClient = dbServiceClient;
        this.dbServiceAddress = dbServiceAddress;
        this.dBServicePhone = dBServicePhone;
    }

    @Override
    public void run(String... args) {
        Address address = new Address(null, "123 Main St");
        Address savedAddress = dbServiceAddress.saveAddress(address);
        log.info("Saved address: {}", savedAddress);

        Client client = new Client(
                null,
                "dbServiceFirst",
                savedAddress.id(),
                new HashSet<>()
        );
        Client savedClient = dbServiceClient.saveClient(client);
        log.info("Saved client: {}", savedClient);

        Phone phone1 = new Phone(null, "123-456-7890", savedClient.id());
        Phone phone2 = new Phone(null, "098-765-4321", savedClient.id());

        dBServicePhone.savePhone(phone1);
        dBServicePhone.savePhone(phone2);
        log.info("Saved phones: {}, {}", phone1, phone2);


        Client clientSelected = dbServiceClient.getClient(savedClient.id())
                .orElseThrow(() -> new RuntimeException("Client not found, id: " + savedClient.id()));
        log.info("clientSecondSelected: {}", clientSelected);


        Client updatedClient = new Client(
                clientSelected.id(),
                "dbServiceSecondUpdated",
                clientSelected.addressId(),
                clientSelected.phones()
        );
        dbServiceClient.saveClient(updatedClient);
        log.info("Updated client: {}", updatedClient);


        log.info("All clients");
        dbServiceClient.findAll().forEach(clientItem -> log.info("client:{}", clientItem));
    }

}
