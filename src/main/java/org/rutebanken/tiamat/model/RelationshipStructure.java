

package org.rutebanken.tiamat.model;

import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


@MappedSuperclass
public class RelationshipStructure {

	@Id
    protected String id;

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

}
