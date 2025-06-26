package ru.otus.cachehw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {

    private static final Logger log = LoggerFactory.getLogger(MyCache.class);

    private final Map<K, V> cache = new WeakHashMap<>();

    private final List<HwListener<K, V>> listeners = new ArrayList<>();


    @Override
    public void put(K key, V value) {
        cache.put(key, value);
        notifyAllListeners(key, value, "PUT");
    }

    @Override
    public void remove(K key) {
        V value = cache.remove(key);
        notifyAllListeners(key, value, "REMOVE");
    }

    @Override
    public V get(K key) {
        V value = cache.get(key);
        notifyAllListeners(key, value, "GET");
        return value;
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void notifyAllListeners(K key, V value, String action) {
        for (HwListener<K, V> listener : listeners) {
            notifyListener(listener, key, value, action);
        }
    }

    private void notifyListener(HwListener<K, V> listener, K key, V value, String action) {
        try {
            listener.notify(key, value, action);
        } catch (Exception e) {
            log.error("Listener notification failed: {} ", e.getMessage());
        }
    }

}
