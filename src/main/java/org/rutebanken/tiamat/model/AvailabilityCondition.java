package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;

@Entity
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "seq_availability_condition" )
public class AvailabilityCondition
        extends AvailabilityCondition_VersionStructure {


}
