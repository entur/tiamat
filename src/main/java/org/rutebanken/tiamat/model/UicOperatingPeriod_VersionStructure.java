package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class UicOperatingPeriod_VersionStructure
        extends OperatingPeriod_VersionStructure {

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
