package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TurnaroundTimeLimitTimes_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<TurnaroundTimeLimitTime> turnaroundTimeLimitTime;

    public List<TurnaroundTimeLimitTime> getTurnaroundTimeLimitTime() {
        if (turnaroundTimeLimitTime == null) {
            turnaroundTimeLimitTime = new ArrayList<TurnaroundTimeLimitTime>();
        }
        return this.turnaroundTimeLimitTime;
    }

}
