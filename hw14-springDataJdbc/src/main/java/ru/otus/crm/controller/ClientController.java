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
import java.util.List;
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

        List<ClientDetails> clientDetailsList = new ArrayList<>();

        for (Client client : clients) {

            Address address = null;
            if (client.addressId() != null) {
                address = addressService.getAddress(client.addressId()).orElse(null);
            }

            List<Phone> phones = phoneService.findPhonesByClientId(client.id());

            ClientDetails clientDetails = new ClientDetails(
                    client.id(),
                    client.name(),
                    address,
                    phones
            );

            clientDetailsList.add(clientDetails);
        }

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

        Client client;
        if (id != null) {

            Client existingClient = clientService.getClient(id)
                    .orElseThrow(() -> new RuntimeException("Client not found"));


            Address address = existingClient.addressId() != null ?
                    addressService.getAddress(existingClient.addressId())
                            .map(a -> new Address(a.id(), street))
                            .orElse(new Address(null, street)) :
                    new Address(null, street);

            address = addressService.saveAddress(address);

            client = new Client(id, name, address.id(), null);
            client = clientService.saveClient(client);


            phoneService.findPhonesByClientId(client.id())
                    .forEach(phone -> phoneService.deletePhone(phone.id()));

        } else {

            Address address = addressService.saveAddress(new Address(null, street));
            client = clientService.saveClient(new Client(null, name, address.id(), null));
        }


        if (phones != null && !phones.trim().isEmpty()) {
            String[] phoneNumbers = phones.split(",");
            for (String number : phoneNumbers) {
                if (!number.trim().isEmpty()) {
                    phoneService.savePhone(new Phone(null, number.trim(), client.id()));
                }
            }
        }

        return "redirect:/clients";
    }

    @GetMapping("/clients/edit/{id}")
    public String showEditForm(@PathVariable("id") long id, Model model) {
        Client client = clientService.getClient(id)
                .orElseThrow(() -> new RuntimeException("Client not found"));

        Address address = client.addressId() != null ?
                addressService.getAddress(client.addressId()).orElse(null) : null;

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

            if (client.addressId() != null) {
                addressService.deleteAddress(client.addressId());
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
