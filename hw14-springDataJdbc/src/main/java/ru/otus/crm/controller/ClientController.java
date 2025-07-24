package ru.otus.crm.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
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

import java.util.*;
import java.util.stream.Collectors;

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
        List<Client> clients = clientService.findAll();

        List<ClientDetails> clientDetailsList = clients.stream()
                .map(client -> {
                    Address address = null;
                    if (client.address() != null && client.address().getId() != null) {
                        address = addressService.getAddress(client.address().getId()).orElse(null);
                    }

                    List<Phone> phones = client.phones() != null ?
                            new ArrayList<>(client.phones()) :
                            Collections.emptyList();

                    return new ClientDetails(
                            client.id(),
                            client.name(),
                            address,
                            phones
                    );
                }).toList();

        model.addAttribute("clientDetailsList", clientDetailsList);
        return "clients";
    }

    public record ClientDetails(
            Long id,
            String name,
            Address address,
            List<Phone> phones
    ) {
    }

    @GetMapping("/clients/new")
    public String showNewClientForm(Model model) {
        model.addAttribute("client", new Client(null, "", null, null));
        return "client_form";
    }

    @PostMapping("/clients/save")
    public String saveClient(
            @RequestParam(value = "id", required = false) Long id,
            @RequestParam("name") String name,
            @RequestParam(value = "street", required = false) String street,
            @RequestParam(value = "phones", required = false) String phones) {

        Set<Phone> phoneSet = new HashSet<>();
        if (phones != null && !phones.trim().isEmpty()) {
            String[] phoneNumbers = phones.split(",");
            for (String number : phoneNumbers) {
                if (!number.trim().isEmpty()) {
                    phoneSet.add(new Phone(null, number.trim(), null));
                }
            }
        }

        if (id != null) {
            Client existingClient = clientService.getClient(id)
                    .orElseThrow(() -> new RuntimeException("Client not found"));

            Address address = existingClient.address() != null && existingClient.address().getId() != null ?
                    addressService.getAddress(existingClient.address().getId())
                            .map(a -> new Address(a.id(), street))
                            .orElse(new Address(null, street)) :
                    new Address(null, street);
            address = addressService.saveAddress(address);

            Client updatedClient = new Client(
                    id,
                    name,
                    AggregateReference.to(address.id()),
                    phoneSet
            );
            clientService.saveClient(updatedClient);
        } else {
            Address address = addressService.saveAddress(new Address(null, street));
            Client newClient = new Client(
                    null,
                    name,
                    AggregateReference.to(address.id()),
                    phoneSet
            );
            clientService.saveClient(newClient);
        }

        return "redirect:/clients";
    }

    @GetMapping("/clients/edit/{id}")
    public String showEditForm(@PathVariable("id") long id, Model model) {
        Client client = clientService.getClient(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Address address = client.address() != null && client.address().getId() != null ?
                addressService.getAddress(client.address().getId()).orElse(null) : null;

        List<Phone> phones = phoneService.findPhonesByClientId(client.id());
        String phoneNumbers = phones.stream()
                .map(Phone::number)
                .collect(Collectors.joining(","));

        model.addAttribute("client", client);
        model.addAttribute("address", address);
        model.addAttribute("phones", phoneNumbers);
        return "client_form";
    }

    @GetMapping("/clients/delete/{id}")
    public String deleteClient(@PathVariable("id") long id) {
        clientService.getClient(id).ifPresent(client -> {

            phoneService.findPhonesByClientId(client.id())
                    .forEach(phone -> phoneService.deletePhone(phone.id()));

            clientService.deleteClient(id);

            if (client.address() != null && client.address().getId() != null) {
                addressService.deleteAddress(client.address().getId());
            }
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

}
