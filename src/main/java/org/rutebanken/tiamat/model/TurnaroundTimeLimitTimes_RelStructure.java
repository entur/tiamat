

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TurnaroundTimeLimitTimes_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<TurnaroundTimeLimitTime> turnaroundTimeLimitTime;

    public List<TurnaroundTimeLimitTime> getTurnaroundTimeLimitTime() {
        if (turnaroundTimeLimitTime == null) {
            turnaroundTimeLimitTime = new ArrayList<TurnaroundTimeLimitTime>();
        }
        return this.turnaroundTimeLimitTime;
    }

}
