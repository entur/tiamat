package org.rutebanken.tiamat.model;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.Entity;


@Entity
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class AccessibilityAssessment
        extends AccessibilityAssessment_VersionedChildStructure {


}
