package ru.otus.crm.model;

import jakarta.annotation.Nonnull;
import org.springframework.data.annotation.Id;
import org.springframework.data.jdbc.core.mapping.AggregateReference;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;


@Table("client")
public record Client(
        @Id Long id,
        @Nonnull String name,
        @Column("address_id") AggregateReference<Address, Long> address,
        @MappedCollection(idColumn = "client_id") Set<Phone> phones
) { }
