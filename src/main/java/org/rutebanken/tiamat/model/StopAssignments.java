

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class StopAssignments {

    protected List<PassengerStopAssignment> passengerStopAssignment;

    public List<PassengerStopAssignment> getPassengerStopAssignment() {
        if (passengerStopAssignment == null) {
            passengerStopAssignment = new ArrayList<PassengerStopAssignment>();
        }
        return this.passengerStopAssignment;
    }

}
