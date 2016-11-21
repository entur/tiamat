

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


    "validDayBits",
public class UicOperatingPeriod_VersionStructure
    extends OperatingPeriod_VersionStructure
{

    protected String validDayBits;
    protected List<DayOfWeekEnumeration> daysOfWeek;

    public String getValidDayBits() {
        return validDayBits;
    }

    public void setValidDayBits(String value) {
        this.validDayBits = value;
    }

    public List<DayOfWeekEnumeration> getDaysOfWeek() {
        if (daysOfWeek == null) {
            daysOfWeek = new ArrayList<DayOfWeekEnumeration>();
        }
        return this.daysOfWeek;
    }

}
