package com.dummynode.cryptotrackingbackend.componet.order;

import java.util.concurrent.ConcurrentLinkedDeque;

import org.springframework.stereotype.Component;

@Component
public class TransactionQueue<T extends Order> {
    private ConcurrentLinkedDeque<T> queue = new ConcurrentLinkedDeque<>();

    public void enqueue(T order) {
        queue.addLast(order);
    }

    public T dequeue() {
        if (isEmpty()) {
            return null;
        }
        return queue.removeFirst();
    }

    public T peek() {
        if (isEmpty()) {
            return null;
        }
        return queue.getFirst();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}
