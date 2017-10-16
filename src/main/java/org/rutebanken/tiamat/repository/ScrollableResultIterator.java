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

package org.rutebanken.tiamat.repository;

import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ScrollableResultIterator<T> implements Iterator<T> {
    private static final Logger logger = LoggerFactory.getLogger(ScrollableResultIterator.class);
    private final ScrollableResults scrollableResults;
    private final int fetchSize;
    private final Session session;
    private int counter;
    private Optional<T> next = Optional.empty();

    public ScrollableResultIterator(ScrollableResults scrollableResults, int fetchSize, Session session) {
        this.scrollableResults = scrollableResults;
        this.fetchSize = fetchSize;
        this.session = session;
        counter = 0;
    }

    @Override
    public boolean hasNext() {
        if(next.isPresent()) {
            // Next value was already fetched
            return true;
        }
        next = getNext();
        if (next.isPresent()) {
            // Next value is now fetched. It is present.
            return true;
        }

        next = Optional.empty();
        close();
        return false;
    }

    @Override
    public T next() {
        if(!next.isPresent()) {
            next = getNext();
        }

        if (next.isPresent()) {
            T returnValue =  next.get();
            if (++counter % fetchSize == 0) {
                logger.info("Scrolling {}s. Counter is currently at {}. {}", next.get().getClass().getSimpleName(), counter, session.getStatistics());
            }

            next = Optional.empty();
            return returnValue;
        }

        close();
        throw new NoSuchElementException();
    }

    @SuppressWarnings("unchecked")
    private Optional<T> getNext() {
        evictBeforeNext();
        if (scrollableResults.next() && scrollableResults.get() != null && scrollableResults.get().length > 0) {
            return Optional.of((T) scrollableResults.get()[0]);
        } else {
            return Optional.empty();
        }
    }

    private void evictBeforeNext() {
        if(next.isPresent()) {
            session.evict(next.get());
        }
    }

    private void close() {
        logger.info("Closing result set. Counter ended at {}", counter);
        scrollableResults.close();
    }
}