package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;
import java.time.Instant;

@Embeddable
public class ValidBetween  {

    private Instant fromDate;

    private Instant toDate;

    public ValidBetween(Instant fromDate, Instant toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public ValidBetween(Instant fromDate) {
        this.fromDate = fromDate;
    }

    public ValidBetween() {
    }

    public Instant getFromDate() {
        return fromDate;
    }

    public void setFromDate(Instant fromDate) {
        this.fromDate = fromDate;
    }

    public Instant getToDate() {
        return toDate;
    }

    public void setToDate(Instant toDate) {
        this.toDate = toDate;
    }
}
