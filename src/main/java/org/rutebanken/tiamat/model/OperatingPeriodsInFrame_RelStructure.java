

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


public class OperatingPeriodsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<OperatingPeriod_VersionStructure> operatingPeriodOrUicOperatingPeriod;

    public List<OperatingPeriod_VersionStructure> getOperatingPeriodOrUicOperatingPeriod() {
        if (operatingPeriodOrUicOperatingPeriod == null) {
            operatingPeriodOrUicOperatingPeriod = new ArrayList<OperatingPeriod_VersionStructure>();
        }
        return this.operatingPeriodOrUicOperatingPeriod;
    }

}
