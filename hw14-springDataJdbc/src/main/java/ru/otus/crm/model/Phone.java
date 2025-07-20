package ru.otus.crm.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


@Table("phone")
public record Phone(
        @Id Long id,
        String number,
        @Column("client_id") Long clientId
) {
}
