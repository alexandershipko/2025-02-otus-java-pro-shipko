package ru.otus.crm.service;

import ru.otus.crm.model.SystemUser;

import java.util.List;
import java.util.Optional;

public interface DBServiceSystemUser {

    SystemUser saveSystemUser(SystemUser systemUser);

    Optional<SystemUser> getSystemUser(long id);

    Optional<SystemUser> getSystemUser(String userName);

    List<SystemUser> findAll();
}
