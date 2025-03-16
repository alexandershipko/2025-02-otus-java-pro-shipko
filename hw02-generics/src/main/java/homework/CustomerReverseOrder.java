package homework;

import java.util.ArrayDeque;
import java.util.Deque;


public class CustomerReverseOrder {

    private final Deque<Customer> stack = new ArrayDeque<>();

    public void add(Customer customer) {
        if (customer != null) {
            stack.addFirst(customer);
        }
    }

    public Customer take() {
        return stack.pollFirst();
    }

}