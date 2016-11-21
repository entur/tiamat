

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


@Entity
public class Accesses_RelStructure
    extends ContainmentAggregationStructure
{

    @Column
    @ElementCollection(targetClass=AccessRefStructure.class)
    protected List<AccessRefStructure> accessRefOrAccess;

    public List<AccessRefStructure> getAccessRefOrAccess() {
        if (accessRefOrAccess == null) {
            accessRefOrAccess = new ArrayList<AccessRefStructure>();
        }
        return this.accessRefOrAccess;
    }

}
