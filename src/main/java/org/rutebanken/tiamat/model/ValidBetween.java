package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("from", fromDate)
                .add("to", toDate)
                .toString();
    }
}
