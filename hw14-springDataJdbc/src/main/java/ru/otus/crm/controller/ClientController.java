package ru.otus.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.service.DBServiceAddress;
import ru.otus.crm.service.DBServiceClient;
import ru.otus.crm.service.DBServicePhone;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
@RequiredArgsConstructor
public class ClientController {

    private final DBServiceClient clientService;
    private final DBServiceAddress addressService;
    private final DBServicePhone phoneService;

    @GetMapping("/")
    public String index() {
        return "redirect:/clients";
    }

    // Client operations
    @GetMapping("/clients")
    public String getAllClients(Model model) {
        List<Client> clients = clientService.findAll().stream()
                .sorted(Comparator.comparing(Client::getId, Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        List<ClientDetails> clientDetailsList = clients.stream()
                .map(client -> new ClientDetails(
                        client.getId(),
                        client.getName(),
                        new ArrayList<>(client.getAddresses()),
                        new ArrayList<>(client.getPhones())
                ))
                .toList();

        model.addAttribute("clientDetailsList", clientDetailsList);

        return "clients";
    }

    @GetMapping("/clients/new")
    public String showNewClientForm(Model model) {
        model.addAttribute("client", new Client(null, "", new HashSet<>(), new HashSet<>(), true));

        return "client_form";
    }

    @PostMapping("/clients/save")
    public String saveClient(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "addresses", required = false) List<String> addressesInput,
            @RequestParam(value = "phones", required = false) List<String> phonesInput) {

        Client client;
        Set<Address> addresses = new HashSet<>();

        if (addressesInput != null) {
            for (String street : addressesInput) {
                if (street != null && !street.trim().isEmpty()) {
                    addresses.add(new Address(null, street.trim(), id));
                }
            }
        }

        Set<Phone> phones = new HashSet<>();
        if (phonesInput != null) {
            for (String number : phonesInput) {
                if (number != null && !number.trim().isEmpty()) {
                    phones.add(new Phone(null, number.trim(), id));
                }
            }
        }

        if (id != null) {
            client = clientService.getClient(id)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            client = new Client(
                    client.getId(),
                    name,
                    addresses,
                    phones,
                    client.isNew()
            );
        } else {
            client = new Client(null, name, addresses, phones, true);
        }

        clientService.saveClient(client);

        return "redirect:/clients";
    }

    @GetMapping("/clients/edit/{id}")
    public String showEditClientForm(@PathVariable("id") Long id, Model model) {
        Client client = clientService.getClient(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        List<String> addresses = client.getAddresses().stream()
                .map(Address::street)
                .toList();

        List<String> phones = client.getPhones().stream()
                .map(Phone::number)
                .toList();

        model.addAttribute("client", client);
        model.addAttribute("addresses", addresses);
        model.addAttribute("phones", phones);

        return "client_form";
    }

    @GetMapping("/clients/delete/{id}")
    public String deleteClient(@PathVariable("id") long id) {
        clientService.getClient(id).ifPresent(client -> {
            phoneService.deleteAllByClientId(id);
            addressService.deleteAllByClientId(id);

            clientService.deleteClient(id);
        });

        return "redirect:/clients";
    }

    // Address operations
    @GetMapping("/addresses")
    public String getAllAddresses(Model model) {
        List<Address> addresses = addressService.findAll();
        model.addAttribute("addresses", addresses);

        return "addresses";
    }

    @GetMapping("/addresses/{id}")
    public String getAddress(@PathVariable("id") long id, Model model) {
        Address address = addressService.getAddress(id)
                .orElseThrow(() -> new RuntimeException("Address not found"));
        model.addAttribute("address", address);

        return "address";
    }

    // Phone operations
    @GetMapping("/phones")
    public String getAllPhones(Model model) {
        List<Phone> phones = phoneService.findAll();
        model.addAttribute("phones", phones);

        return "phones";
    }

    @GetMapping("/phones/client/{clientId}")
    public String getPhonesByClient(@PathVariable("clientId") long clientId, Model model) {
        List<Phone> phones = phoneService.findPhonesByClientId(clientId);
        model.addAttribute("phones", phones);

        return "phones";
    }


    public record ClientDetails(
            Long id,
            String name,
            List<Address> addresses,
            List<Phone> phones
    ) {
    }

}
