package org.rutebanken.tiamat.repository;

import org.springframework.data.domain.Pageable;

import java.time.Instant;

public class ChangedStopPlaceSearch {

    private Instant from;

    private Instant to;

    private Pageable pageable;

    public ChangedStopPlaceSearch(Instant from, Instant to, Pageable pageable) {
        this.from = from;
        this.to = to;
        this.pageable = pageable;
    }

    public Instant getFrom() {
        return from;
    }

    public Instant getTo() {
        return to;
    }

    public Pageable getPageable() {
        return pageable;
    }
}
