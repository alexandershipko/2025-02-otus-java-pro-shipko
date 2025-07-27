package ru.otus.crm.model;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;


@Table("client")
@Getter
public class Client {

    @Id
    @Nonnull
    private final Long id;

    private final String name;

    @MappedCollection(idColumn = "client_id")
    private final Set<Address> addresses;

    @MappedCollection(idColumn = "client_id")
    private final Set<Phone> phones;

    public Client(Long id, String name, Set<Address> addresses, Set<Phone> phones) {
        this.id = id;
        this.name = name;
        this.addresses = addresses;
        this.phones = phones;
    }

    @Override
    public String toString() {
        return "Client{" + "id='"
                + id + '\'' + ", name='"
                + name + '\'' + ", addresses="
                + addresses + ", phones="
                + phones + '}';
    }

}
