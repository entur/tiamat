package org.rutebanken.tiamat.model;

import javax.persistence.AttributeOverride;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import java.time.ZonedDateTime;

@Entity
public class ValidBetween extends ValidityCondition {

    private ZonedDateTime fromDate;

    private ZonedDateTime toDate;

    public ValidBetween(ZonedDateTime fromDate, ZonedDateTime toDate) {
        this.fromDate = fromDate;
        this.toDate = toDate;
    }

    public ValidBetween(ZonedDateTime fromDate) {
        this.fromDate = fromDate;
    }

    public ValidBetween() {
    }

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
