package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import java.time.ZonedDateTime;

@Entity
public class AvailabilityCondition extends ValidBetween {

    public AvailabilityCondition(ZonedDateTime fromDate, ZonedDateTime toDate) {
        super(fromDate, toDate);
    }

    public AvailabilityCondition(ZonedDateTime fromDate) {
        super(fromDate);
    }

    public AvailabilityCondition() {
    }
}
