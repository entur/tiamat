package org.rutebanken.tiamat.model;

import sun.java2d.pipe.SpanShapeRenderer;

import javax.persistence.*;
import javax.xml.bind.annotation.*;
import java.util.Objects;

@Entity
public class SimplePoint
    extends EntityInVersionStructure
{

    public SimplePoint(LocationStructure location) {
        this.location = location;
    }

    public SimplePoint() {
    }

    @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    protected LocationStructure location;

    public LocationStructure getLocation() {
        return location;
    }

    public void setLocation(LocationStructure value) {
        this.location = value;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null) return false;
        if(!(object instanceof SimplePoint)) return false;

        SimplePoint other = (SimplePoint) object;
        return Objects.equals(this.location, other.location);
    }

    @Override
    public int hashCode() {
        return Objects.hash(location);
    }
}
