package org.rutebanken.tiamat.model;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.MappedSuperclass;


@MappedSuperclass
public class AccessibilityLimitation_VersionedChildStructure
        extends VersionedChildStructure {

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration wheelchairAccess;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration stepFreeAccess;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration escalatorFreeAccess;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration liftFreeAccess;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration audibleSignalsAvailable;

    @Enumerated(EnumType.STRING)
    protected LimitationStatusEnumeration visualSignsAvailable;

    public LimitationStatusEnumeration getWheelchairAccess() {
        return wheelchairAccess;
    }

    public void setWheelchairAccess(LimitationStatusEnumeration value) {
        this.wheelchairAccess = value;
    }

    public LimitationStatusEnumeration getStepFreeAccess() {
        return stepFreeAccess;
    }

    public void setStepFreeAccess(LimitationStatusEnumeration value) {
        this.stepFreeAccess = value;
    }

    public LimitationStatusEnumeration getEscalatorFreeAccess() {
        return escalatorFreeAccess;
    }

    public void setEscalatorFreeAccess(LimitationStatusEnumeration value) {
        this.escalatorFreeAccess = value;
    }

    public LimitationStatusEnumeration getLiftFreeAccess() {
        return liftFreeAccess;
    }

    public void setLiftFreeAccess(LimitationStatusEnumeration value) {
        this.liftFreeAccess = value;
    }

    public LimitationStatusEnumeration getAudibleSignalsAvailable() {
        return audibleSignalsAvailable;
    }

    public void setAudibleSignalsAvailable(LimitationStatusEnumeration value) {
        this.audibleSignalsAvailable = value;
    }

    public LimitationStatusEnumeration getVisualSignsAvailable() {
        return visualSignsAvailable;
    }

    public void setVisualSignsAvailable(LimitationStatusEnumeration value) {
        this.visualSignsAvailable = value;
    }

}
