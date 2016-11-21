package org.rutebanken.tiamat.model;

import javax.xml.datatype.Duration;


public class OnboardStay_VersionedChlldStructure
        extends VersionedChildStructure {
    protected BoardingPermissionEnumeration boardingPermisssion;
    protected Duration period;

    public BoardingPermissionEnumeration getBoardingPermisssion() {
        return boardingPermisssion;
    }

    public void setBoardingPermisssion(BoardingPermissionEnumeration value) {
        this.boardingPermisssion = value;
    }

    public Duration getPeriod() {
        return period;
    }

    public void setPeriod(Duration value) {
        this.period = value;
    }

}
