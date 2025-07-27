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
import java.util.Set;


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

        Client newClient = dbServiceClient.saveClient(
                new Client(null, "John Smith", new HashSet<>(), new HashSet<>())
        );

        Address homeAddress = dbServiceAddress.saveAddress(
                new Address(null, "123 Main St", newClient.getId())
        );
        Address workAddress = dbServiceAddress.saveAddress(
                new Address(null, "456 Work Ave", newClient.getId())
        );

        Phone mobilePhone = dBServicePhone.savePhone(
                new Phone(null, "+1234567890", newClient.getId())
        );
        Phone workPhone = dBServicePhone.savePhone(
                new Phone(null, "+1987654321", newClient.getId())
        );

        Client clientWithContacts = new Client(
                newClient.getId(),
                newClient.getName(),
                new HashSet<>(Set.of(homeAddress, workAddress)),
                new HashSet<>(Set.of(mobilePhone, workPhone))
        );
        Client savedClient = dbServiceClient.saveClient(clientWithContacts);

        Client existingClient = dbServiceClient.getClient(savedClient.getId())
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Set<Address> currentAddresses = new HashSet<>(existingClient.getAddresses());
        Set<Phone> currentPhones = new HashSet<>(existingClient.getPhones());


        Address oldHomeAddress = currentAddresses.stream()
                .filter(a -> a.street().equals("123 Main St"))
                .findFirst()
                .orElseThrow();
        currentAddresses.remove(oldHomeAddress);
        currentAddresses.add(
                dbServiceAddress.saveAddress(
                        new Address(oldHomeAddress.id(), "789 New Home St", existingClient.getId())
                )
        );


        Phone oldMobilePhone = currentPhones.stream()
                .filter(p -> p.number().equals("+1234567890"))
                .findFirst()
                .orElseThrow();
        currentPhones.remove(oldMobilePhone);
        currentPhones.add(
                dBServicePhone.savePhone(
                        new Phone(oldMobilePhone.id(), "+1112223333", existingClient.getId())
                )
        );

        Client updatedClient = new Client(
                existingClient.getId(),
                "John Smith Jr.",
                currentAddresses,
                currentPhones
        );

        Client finalResult = dbServiceClient.saveClient(updatedClient);
        log.info("Final client data: {}", finalResult);
    }

}
