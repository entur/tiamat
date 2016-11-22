package org.rutebanken.tiamat.service;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.junit.Test;
import org.rutebanken.tiamat.config.GeometryFactoryConfig;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;

public class CentroidComputerTest {

    private GeometryFactory geometryFactory = new GeometryFactoryConfig().geometryFactory();
    private CentroidComputer centroidComputer = new CentroidComputer(geometryFactory);

    @Test
    public void computeCentroidForStopPlaceWithQuayNullpointer() throws Exception {
        StopPlace stopPlace = new StopPlace();

        Quay quay = new Quay();
        quay.setCentroid(geometryFactory.createPoint(new Coordinate(10, 10)));

        stopPlace.getQuays().add(quay);

        centroidComputer.computeCentroidForStopPlace(stopPlace);
    }
}