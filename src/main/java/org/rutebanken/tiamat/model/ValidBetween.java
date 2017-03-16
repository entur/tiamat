package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

@MappedSuperclass
public class ValidBetween extends ValidityCondition {

    private ZonedDateTime fromDate;

    private ZonedDateTime toDate;


    public ZonedDateTime getFromDate() {
        return fromDate;
    }

    public void setFromDate(ZonedDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public ZonedDateTime getToDate() {
        return toDate;
    }

    public void setToDate(ZonedDateTime toDate) {
        this.toDate = toDate;
    }
}
