package org.rutebanken.tiamat.model;

import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

public class TopographicPlace
    extends TopographicPlace_VersionStructure
{

    public TopographicPlace(EmbeddableMultilingualString name) {
        super(name);
    }

    public TopographicPlace() {}
}
