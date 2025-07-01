package ru.otus.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.SystemUser;

import java.util.List;
import java.util.Optional;

public class DBServiceSystemUserImpl implements DBServiceSystemUser {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<SystemUser> systemUserDataTemplate;
    private final TransactionManager transactionManager;

    public DBServiceSystemUserImpl(TransactionManager transactionManager, DataTemplate<SystemUser> systemUserDataTemplate) {
        this.transactionManager = transactionManager;
        this.systemUserDataTemplate = systemUserDataTemplate;
    }

    @Override
    public SystemUser saveSystemUser(SystemUser systemUser) {
        return transactionManager.doInTransaction(session -> {
            var systemUserCloned = systemUser.clone();
            if (systemUserCloned.getId() == null) {
                var savedSystemUser = systemUserDataTemplate.insert(session, systemUserCloned);
                log.info("created systemUser: {}", systemUserCloned);
                return savedSystemUser;
            }
            var savedSystemUser = systemUserDataTemplate.update(session, systemUserCloned);
            log.info("updated systemUser: {}", savedSystemUser);
            return savedSystemUser;
        });
    }

    @Override
    public Optional<SystemUser> getSystemUser(long id) {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var savedSystemUserOptional = systemUserDataTemplate.findById(session, id);
            log.info("client: {}", savedSystemUserOptional);
            return savedSystemUserOptional;
        });
    }

    @Override
    public Optional<SystemUser> getSystemUser(String userName) {
        return transactionManager.doInReadOnlyTransaction(session ->
                systemUserDataTemplate.findByEntityField(session, "userName", userName).stream()
                        .findFirst()
                        .map(SystemUser::clone)
        );
    }

    @Override
    public List<SystemUser> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var systemUserList = systemUserDataTemplate.findAll(session);
            log.info("systemUserList: {}", systemUserList);
            return systemUserList;
        });
    }
}
