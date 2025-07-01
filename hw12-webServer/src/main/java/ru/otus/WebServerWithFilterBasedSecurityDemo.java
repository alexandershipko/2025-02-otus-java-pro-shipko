package ru.otus;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.hibernate.cfg.Configuration;
import ru.otus.core.repository.DataTemplateHibernate;
import ru.otus.core.repository.HibernateUtils;
import ru.otus.core.sessionmanager.TransactionManagerHibernate;
import ru.otus.crm.dbmigrations.MigrationsExecutorFlyway;
import ru.otus.crm.model.Address;
import ru.otus.crm.model.Client;
import ru.otus.crm.model.Phone;
import ru.otus.crm.model.SystemUser;
import ru.otus.crm.service.DBServiceSystemUserImpl;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.server.UsersWebServer;
import ru.otus.server.UsersWebServerWithFilterBasedSecurity;
import ru.otus.services.TemplateProcessor;
import ru.otus.services.TemplateProcessorImpl;
import ru.otus.services.UserAuthService;
import ru.otus.services.UserAuthServiceImpl;

import static ru.otus.DbServiceDemo.HIBERNATE_CFG_FILE;

/*
    Полезные для демо ссылки

    // Стартовая страница
    http://localhost:8080

    // Страница пользователей
    http://localhost:8080/users

    // REST сервис
    http://localhost:8080/api/user/3
*/
public class WebServerWithFilterBasedSecurityDemo {
    private static final int WEB_SERVER_PORT = 8080;
    private static final String TEMPLATES_DIR = "/templates/";

    public static void main(String[] args) throws Exception {
        var configuration = new Configuration().configure(HIBERNATE_CFG_FILE);

        var dbUrl = configuration.getProperty("hibernate.connection.url");
        var dbUserName = configuration.getProperty("hibernate.connection.username");
        var dbPassword = configuration.getProperty("hibernate.connection.password");

        new MigrationsExecutorFlyway(dbUrl, dbUserName, dbPassword).executeMigrations();

        var sessionFactory = HibernateUtils.buildSessionFactory(configuration, Client.class, Address.class, Phone.class, SystemUser.class);

        var transactionManager = new TransactionManagerHibernate(sessionFactory);

        var clientTemplate = new DataTemplateHibernate<>(Client.class);
        var dbServiceClient = new DbServiceClientImpl(transactionManager, clientTemplate);

        var systemUserTemplate = new DataTemplateHibernate<>(SystemUser.class);
        var dbServiceSystemUser = new DBServiceSystemUserImpl(transactionManager, systemUserTemplate);

        var systemUser = new SystemUser(null, "admin2", "password2", "admin");
        var savedSystemUser = dbServiceSystemUser.saveSystemUser(systemUser);

        //UserDao userDao = new InMemoryUserDao();
        Gson gson = new GsonBuilder().serializeNulls().setPrettyPrinting().create();
        TemplateProcessor templateProcessor = new TemplateProcessorImpl(TEMPLATES_DIR);
        UserAuthService authService = new UserAuthServiceImpl(dbServiceSystemUser);

        UsersWebServer usersWebServer = new UsersWebServerWithFilterBasedSecurity(
                WEB_SERVER_PORT, authService, dbServiceSystemUser, gson, templateProcessor);

        usersWebServer.start();
        usersWebServer.join();
    }
}



//
//        Домашнее задание:
//
//❖Встроить веб-сервер в приложение из ДЗ про
//Hibernate ORM (или в пример из вебинара встроить
//ДЗ про Hibernate :))
//
//
//❖Сделать стартовую страницу, на которой админ
//должен аутентифицироваться
//
//
//❖Сделать админскую страницу для работы с
//        клиентами. На этой странице должны быть
//        доступны следующие функции:
//        ▪ создать клиента
//▪ получить список клиентов
//
//
//❖Доменная модель должна остаться от ДЗ с Hibernate
//
//
//❖Это клиент с адресом и телефонами
//
//
//❖Используем абстракции для работы с БД из примера по
//        Hibernate. Там нет DAO, там сервис и темплейт
//
//
//❖Юзер из примера веб-сервера может остаться, но только
//        для входа в приложение. Никаких списков юзеров и/или
//        форм их создания быть не должно
//
//
//❖Все, что не используется из примера веб-сервера, нужно
//        убрать. Например, рандомного пользователя в тексте ДЗ
//        нет
//
//
//❖Так же, нужно оставить только одну реализацию
//        безопасности. Остальные классы в репозиторий класть
//        не нужно