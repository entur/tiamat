package org.rutebanken.tiamat.model.indentification;

import com.fasterxml.jackson.annotation.JsonIgnore;

public interface IdentifiedEntity {

    Long getId();

    void setId(Long id);
}
