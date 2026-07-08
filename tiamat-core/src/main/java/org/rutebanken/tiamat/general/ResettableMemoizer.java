/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
