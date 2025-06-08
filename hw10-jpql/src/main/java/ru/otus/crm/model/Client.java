package ru.otus.crm.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq", initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        //TODO
        throw new UnsupportedOperationException();
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        return new Client(this.id, this.name);
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}


/**
 Домашнее задание
 Использование Hibernate

 Цель:
 На практике освоить основы Hibernate.
 Понять как аннотации-hibernate влияют на формирование sql-запросов.


 Описание/Пошаговая инструкция выполнения домашнего задания:
 Работа должна использовать базу данных в docker-контейнере .

 За основу возьмите пример из вебинара про JPQL (class DbServiceDemo).
 Добавьте в Client поля:
 адрес (OneToOne)
 class Address {
 private String street;
 }
 и телефон (OneToMany)
 class Phone {
 private String number;
 }

 Разметьте классы таким образом, чтобы при сохранении/чтении объека Client каскадно сохранялись/читались вложенные объекты.

 ВАЖНО.

 Hibernate должен создать только три таблицы: для телефонов, адресов и клиентов.
 При сохранении нового объекта не должно быть update-ов.
 Посмотрите в логи и проверьте, что эти два требования выполняются.
 */