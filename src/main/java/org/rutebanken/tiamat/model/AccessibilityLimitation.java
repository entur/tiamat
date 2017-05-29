package org.rutebanken.tiamat.model;

import com.google.common.base.MoreObjects;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;


@Entity
public class AccessibilityLimitation
        extends AccessibilityLimitation_VersionedChildStructure {


    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .omitNullValues()
                .add("netexId", netexId)
                .add("version", version)
                .add("wheelchairAccess", wheelchairAccess)
                .add("stepFreeAccess", stepFreeAccess)
                .add("escalatorFreeAccess", escalatorFreeAccess)
                .add("liftFreeAccess", liftFreeAccess)
                .add("audibleSignalsAvailable", audibleSignalsAvailable)
                .add("visualSignsAvailable", visualSignsAvailable)
                .toString();
    }

}
