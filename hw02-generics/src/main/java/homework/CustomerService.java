package homework;

import java.util.*;


public class CustomerService {

    private final NavigableMap<Customer, String> map = new TreeMap<>(
            Comparator.comparingLong(Customer::getScores)
                    .thenComparingLong(Customer::getId)
    );

    public Map.Entry<Customer, String> getSmallest() {
        if (map.isEmpty()) {
            return null;
        }

        Map.Entry<Customer, String> entry = map.firstEntry();
        Customer copy = new Customer(entry.getKey().getId(), entry.getKey().getName(), entry.getKey().getScores());
        return new AbstractMap.SimpleEntry<>(copy, entry.getValue());
    }

    public Map.Entry<Customer, String> getNext(Customer customer) {
        Map.Entry<Customer, String> entry = map.higherEntry(customer);
        if (entry == null) {
            return null;
        }

        Customer copy = new Customer(entry.getKey().getId(), entry.getKey().getName(), entry.getKey().getScores());
        return new AbstractMap.SimpleEntry<>(copy, entry.getValue());
    }

    public void add(Customer customer, String data) {
        if (customer == null) {
            throw new IllegalArgumentException("Customer cannot be null");
        }
        map.put(customer, data);
    }

}