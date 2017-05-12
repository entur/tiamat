package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import java.time.Instant;

@Entity
public class ValidBetween extends ValidityCondition {

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
