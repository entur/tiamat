package no.rutebanken.tiamat.model;
import javax.persistence.SequenceGenerator;

import javax.persistence.Entity;

@Entity
@SequenceGenerator(initialValue = 1, name = "idgen", sequenceName = "seq_location")
public class Location extends LocationStructure {
}
