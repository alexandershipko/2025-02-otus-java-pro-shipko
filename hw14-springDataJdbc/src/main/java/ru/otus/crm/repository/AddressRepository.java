package ru.otus.crm.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import ru.otus.crm.model.Address;


public interface AddressRepository extends ListCrudRepository<Address, Long> {

    @Modifying
    @Query("DELETE FROM address WHERE client_id = :clientId")
    void deleteAllByClientId(Long clientId);

}