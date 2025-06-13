package ru.otus.crm.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "client", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Phone> phones = new ArrayList<>();


    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Client(Long id, String name, Address address, List<Phone> phones) {
        this.id = id;
        this.name = name;
        this.address = address;
        if (address != null) {
            address.setClient(this);
        }

        this.phones = new ArrayList<>();
        if (phones != null) {
            for (Phone phone : phones) {
                phone.setClient(this);
                this.phones.add(phone);
            }
        }
    }

    @Override
    @SuppressWarnings({"java:S2975", "java:S1182"})
    public Client clone() {
        Client cloned = new Client(this.id, this.name);

        if (this.address != null) {
            Address clonedAddress = this.address.clone();
            cloned.setAddress(clonedAddress);
            clonedAddress.setClient(cloned);
        }

        if (this.phones != null) {
            List<Phone> clonedPhones = this.phones.stream()
                    .map(phone -> {
                        Phone clonedPhone = phone.clone();
                        clonedPhone.setClient(cloned);
                        return clonedPhone;
                    })
                    .toList();
            cloned.setPhones(clonedPhones);
        }

        return cloned;
    }

    @Override
    public String toString() {
        return "Client{" + "id=" + id + ", name='" + name + '\'' + '}';
    }

}
