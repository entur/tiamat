package org.rutebanken.tiamat.model;


import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import javax.persistence.Entity;

@Entity
public class PersistablePolygon extends IdentifiedEntity {

    private Polygon polygon;

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
