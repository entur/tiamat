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
    private Optional<T> currentItem = Optional.empty();

    public ScrollableResultIterator(ScrollableResults scrollableResults, int fetchSize, Session session) {
        this.scrollableResults = scrollableResults;
        this.fetchSize = fetchSize;
        this.session = session;
        counter = 0;
    }

    @Override
    public boolean hasNext() {
        currentItem = getNext();
        if (currentItem.isPresent()) {
            return true;
        }

        close();
        return false;
    }

    @Override
    public T next() {
        if(!currentItem.isPresent()) {
            currentItem = getNext();
        }

        if (currentItem.isPresent()) {
            if (++counter % fetchSize == 0) {
                logger.info("Scrolling stop places. Counter is currently at {}", counter);
            }
            return currentItem.get();
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
        if(currentItem.isPresent()) {
            session.evict(currentItem.get());
        }
    }

    private void close() {
        logger.info("Closing result set. Counter ended at {}", counter);
        scrollableResults.close();
    }
}