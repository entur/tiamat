package org.rutebanken.tiamat.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import java.util.ArrayList;
import java.util.List;


@Entity
public class PathJunctions_RelStructure
        extends ContainmentAggregationStructure {

    @ElementCollection(targetClass = PathJunctionRefStructure.class)
    protected List<PathJunctionRefStructure> pathJunctionRefOrPathJunction;

    public List<PathJunctionRefStructure> getPathJunctionRefOrPathJunction() {
        if (pathJunctionRefOrPathJunction == null) {
            pathJunctionRefOrPathJunction = new ArrayList<>();
        }
        return this.pathJunctionRefOrPathJunction;
    }

}
