package org.rutebanken.tiamat.model;


import com.vividsolutions.jts.geom.Polygon;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class PersistablePolygon  {

    @Id
    @GeneratedValue(generator="sequence_per_table_generator")
    protected Long id;

    private Polygon polygon;

    public Polygon getPolygon() {
        return polygon;
    }

    public void setPolygon(Polygon polygon) {
        this.polygon = polygon;
    }
}
