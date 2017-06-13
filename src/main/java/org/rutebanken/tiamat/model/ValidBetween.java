package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("from", fromDate)
                .add("to", toDate)
                .toString();
    }
}
