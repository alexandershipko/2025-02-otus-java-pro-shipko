package ru.otus.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("address")
public record Address(
        @Id @Column("id") Long id,
        String street,
        @Column("client_id") Long clientId
) {
}
