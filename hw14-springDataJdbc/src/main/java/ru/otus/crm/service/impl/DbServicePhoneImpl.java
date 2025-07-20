package ru.otus.crm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.crm.model.Phone;
import ru.otus.crm.repository.PhoneRepository;
import ru.otus.crm.service.DBServicePhone;
import ru.otus.sessionmanager.TransactionManager;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class DbServicePhoneImpl implements DBServicePhone {

    private final TransactionManager transactionManager;
    private final PhoneRepository phoneRepository;

    public DbServicePhoneImpl(TransactionManager transactionManager, PhoneRepository phoneRepository) {
        this.transactionManager = transactionManager;
        this.phoneRepository = phoneRepository;
    }

    @Override
    public Phone savePhone(Phone phone) {
        return transactionManager.doInTransaction(() -> {
            var savedPhone = phoneRepository.save(phone);
            log.info("Saved phone: {}", savedPhone);
            return savedPhone;
        });
    }

    @Override
    public Optional<Phone> getPhone(long id) {
        var phoneOptional = phoneRepository.findById(id);
        log.info("Phone: {}", phoneOptional);
        return phoneOptional;
    }

    @Override
    public List<Phone> findAll() {
        var phoneList = phoneRepository.findAll();
        log.info("Phone list: {}", phoneList);
        return phoneList;
    }

    @Override
    public List<Phone> findPhonesByClientId(long clientId) {
        var phones = phoneRepository.findByClientId(clientId);
        log.info("Phones for client {}: {}", clientId, phones);
        return phones;
    }

    @Override
    public void deletePhone(long id) {
        transactionManager.doInTransaction(() -> {
            phoneRepository.deleteById(id);
            log.info("Deleted phone with id: {}", id);
            return null;
        });
    }

}
