package ru.otus.crm.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.otus.crm.model.Address;
import ru.otus.crm.repository.AddressRepository;
import ru.otus.crm.service.DBServiceAddress;
import ru.otus.sessionmanager.TransactionManager;

import java.util.List;
import java.util.Optional;


@Slf4j
@Service
public class DbServiceAddressImpl implements DBServiceAddress {

    private final TransactionManager transactionManager;
    private final AddressRepository addressRepository;

    public DbServiceAddressImpl(TransactionManager transactionManager, AddressRepository addressRepository) {
        this.transactionManager = transactionManager;
        this.addressRepository = addressRepository;
    }

    @Override
    public Address saveAddress(Address address) {
        return transactionManager.doInTransaction(() -> {
            var savedAddress = addressRepository.save(address);
            log.info("Saved address: {}", savedAddress);
            return savedAddress;
        });
    }

    @Override
    public Optional<Address> getAddress(long id) {
        var addressOptional = addressRepository.findById(id);
        log.info("Address: {}", addressOptional);
        return addressOptional;
    }

    @Override
    public List<Address> findAll() {
        var addressList = addressRepository.findAll();
        log.info("Address list: {}", addressList);
        return addressList;
    }

    @Override
    public void deleteAddress(long id) {
        transactionManager.doInTransaction(() -> {
            addressRepository.deleteById(id);
            log.info("Deleted address with id: {}", id);
            return null;
        });
    }

}
