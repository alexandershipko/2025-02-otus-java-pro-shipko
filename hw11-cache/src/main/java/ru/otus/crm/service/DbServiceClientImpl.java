package ru.otus.crm.service;

import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    @SuppressWarnings("java:S1604")
    HwListener<String, Client> listener = new HwListener<String, Client>() {
        @Override
        public void notify(String key, Client value, String action) {
            log.info("key:{}, value:{}, action: {}", key, value, action);
        }
    };

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final HwCache<String, Client> cache = new MyCache<>();

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.cache.addListener(listener);
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
    }

    @Override
    public Client saveClient(Client client) {
        return transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();

            if (client.getId() == null) {
                var savedClient = clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);
                cache.put(getCacheKeyForClient(savedClient), savedClient);
                return savedClient;
            }

            var savedClient = clientDataTemplate.update(session, clientCloned);
            log.info("updated client: {}", savedClient);
            cache.put(getCacheKeyForClient(savedClient), savedClient);
            return savedClient;
        });
    }

    @Override
    public Optional<Client> getClient(long id) {
        String cacheKey = String.valueOf(id);
        Client cachedClient = cache.get(cacheKey);

        if (cachedClient != null) {
            log.info("client retrieved from cache: {}", cachedClient);
            return Optional.of(cachedClient);
        }

        return transactionManager.doInReadOnlyTransaction(
                session -> clientDataTemplate.findById(session, id)
                        .map(client -> {
                            cache.put(getCacheKeyForClient(client), client);
                            log.info("client retrieved from DB: {}", client);
                            return Optional.of(client);
                        }).orElseGet(() -> {
                            log.warn("Client not found in DB for id: {}", id);
                            return Optional.empty();
                        }));
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }

    private String getCacheKeyForClient(Client client) {
        return String.valueOf(client.getId());
    }

}
