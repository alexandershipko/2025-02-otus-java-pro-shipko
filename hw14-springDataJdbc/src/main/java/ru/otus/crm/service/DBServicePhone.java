package ru.otus.crm.service;

import ru.otus.crm.model.Phone;

import java.util.List;
import java.util.Optional;


public interface DBServicePhone {

    Phone savePhone(Phone phone);

    Optional<Phone> getPhone(long id);

    List<Phone> findAll();

    List<Phone> findPhonesByClientId(long clientId);

    void deletePhone(long id);

}
