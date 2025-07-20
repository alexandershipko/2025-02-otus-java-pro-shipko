package ru.otus.crm.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import ru.otus.crm.model.Phone;

import java.util.List;


public interface PhoneRepository extends ListCrudRepository<Phone, Long> {

    @Query("SELECT * FROM phone WHERE client_id = :clientId")
    List<Phone> findByClientId(Long clientId);

}
