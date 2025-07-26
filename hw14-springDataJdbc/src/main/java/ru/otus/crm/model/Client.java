package ru.otus.crm.model;

import jakarta.annotation.Nonnull;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceCreator;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;


@Table("client")
@Getter
public class Client implements Persistable<Long> {

    @Id
    @Nonnull
    private final Long id;

    private final String name;

    @MappedCollection(idColumn = "client_id")
    private final Set<Address> addresses;

    @MappedCollection(idColumn = "client_id")
    private final Set<Phone> phones;

    @Transient
    private final boolean isNew;

    public Client(Long id, String name, Set<Address> addresses, Set<Phone> phones, boolean isNew) {
        this.id = id;
        this.name = name;
        this.addresses = addresses;
        this.phones = phones;
        this.isNew = isNew;
    }

    @PersistenceCreator
    private Client(Long id, String name, Set<Address> addresses, Set<Phone> phones) {
        this(id, name, addresses, phones, false);
    }

    @Override
    public boolean isNew() {
        return isNew;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Client{" + "id='"
                + id + '\'' + ", name='"
                + name + '\'' + ", addresses="
                + addresses + ", phones="
                + phones + ", isNew="
                + isNew + '}';
    }

}
