package org.rutebanken.tiamat.model;

import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import javax.persistence.MappedSuperclass;
import java.io.Serializable;


@MappedSuperclass
public abstract class EntityStructure extends IdentifiedEntity implements Serializable {

}
