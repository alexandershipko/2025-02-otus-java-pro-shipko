package ru.otus.services;

import ru.otus.crm.service.DBServiceSystemUser;

public class UserAuthServiceImpl implements UserAuthService {

    private final DBServiceSystemUser dbServiceSystemUser;

    public UserAuthServiceImpl(DBServiceSystemUser dbServiceSystemUser) {
        this.dbServiceSystemUser = dbServiceSystemUser;
    }

    @Override
    public boolean authenticate(String login, String password) {
        return dbServiceSystemUser.getSystemUser(login)
                .map(user -> user.getUserPassword().equals(password))
                .orElse(false);
    }

}
