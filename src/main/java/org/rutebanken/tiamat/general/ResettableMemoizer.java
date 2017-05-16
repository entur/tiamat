package org.rutebanken.tiamat.general;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Supplier;

public class ResettableMemoizer<T> {


    private final Supplier<T> supplier;

    private transient volatile boolean isEmpty = true;

    private T value;

    private final Lock lock = new ReentrantLock();

    public ResettableMemoizer(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    public T get() {
        lock.lock();
        try {
            if(isEmpty) {
                value = supplier.get();
                isEmpty = false;
            }
            return value;

        } finally {
            lock.unlock();
        }
    }

    public void reset() {
        lock.lock();
        try {
            value = null;
            isEmpty = true;
        } finally {
            lock.unlock();
        }
    }
}
