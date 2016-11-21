

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;


@Entity
public class PathJunctions_RelStructure
    extends ContainmentAggregationStructure
{


    protected List<PathJunctionRefStructure> pathJunctionRefOrPathJunction;

    public List<PathJunctionRefStructure> getPathJunctionRefOrPathJunction() {
        if (pathJunctionRefOrPathJunction == null) {
            pathJunctionRefOrPathJunction = new ArrayList<>();
        }
        return this.pathJunctionRefOrPathJunction;
    }

}
