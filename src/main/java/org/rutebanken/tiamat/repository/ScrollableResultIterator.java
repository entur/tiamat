package org.rutebanken.tiamat.repository;

import org.hibernate.ScrollableResults;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Optional;

public class ScrollableResultIterator<T> implements Iterator<T> {
    private static final Logger logger = LoggerFactory.getLogger(ScrollableResultIterator.class);
    private final ScrollableResults scrollableResults;
    private final int fetchSize;
    private int counter;
    private Optional<T> next = Optional.empty();

    public ScrollableResultIterator(ScrollableResults scrollableResults, int fetchSize) {
        this.scrollableResults = scrollableResults;
        this.fetchSize = fetchSize;
        counter = 0;
    }

    @Override
    public boolean hasNext() {
        next = getNext();
        if (next.isPresent()) {
            return true;
        }

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
                logger.info("Scrolling stop places. Counter is currently at {}", counter);
            }
            return next.get();
        }

        close();
        throw new NoSuchElementException();
    }

    private void close() {
        logger.info("Closing result set. Counter ended at {}", counter);
        scrollableResults.close();
    }

    @SuppressWarnings("unchecked")
    private Optional<T> getNext() {
        if (scrollableResults.next() && scrollableResults.get() != null && scrollableResults.get().length > 0) {
            return Optional.of((T) scrollableResults.get()[0]);
        } else {
            return Optional.empty();
        }
    }
}