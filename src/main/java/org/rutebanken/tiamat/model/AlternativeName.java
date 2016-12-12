package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;


@Entity
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "seq_alternative_name")
public class AlternativeName
        extends AlternativeName_VersionedChildStructure {


}
