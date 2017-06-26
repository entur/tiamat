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
            if (++counter % fetchSize == 0) {
                logger.debug("Scrolling {}s. Counter is currently at {}", next.getClass().getSimpleName(), counter);
            }
            T returnValue =  next.get();
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