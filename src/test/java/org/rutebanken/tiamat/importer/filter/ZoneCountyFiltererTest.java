package org.rutebanken.tiamat.importer.filter;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.impl.CoordinateArraySequence;
import org.junit.Ignore;
import org.junit.Test;
import org.onebusaway.gtfs.model.Stop;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class ZoneCountyFiltererTest extends TiamatIntegrationTest {

    @Autowired
    private ZoneCountyFilterer zoneCountyFilterer;

    @Ignore
    @Test
    public void filterByCountyMatch() throws Exception {

        TopographicPlace county1 = new TopographicPlace(new EmbeddableMultilingualString("county1"));
        county1.setTopographicPlaceType(TopographicPlaceTypeEnumeration.COUNTY);

        Point point = geometryFactory.createPoint(new Coordinate(9.84, 59.26));
        Geometry geometry =  point.buffer(20);
        LinearRing linearRing = new LinearRing(new CoordinateArraySequence(geometry.getCoordinates()), geometryFactory);
        county1.setPolygon(geometryFactory.createPolygon(linearRing, null));

        System.out.println("Polygon for county is:"+county1.getPolygon().toString());

        topographicPlaceRepository.save(county1);

        StopPlace stopPlace = new StopPlace();
        stopPlace.setCentroid(point);

        List<? extends Zone_VersionStructure> list = zoneCountyFilterer.filterByCountyMatch(Arrays.asList(county1.getNetexId()), Arrays.asList(stopPlace));

        assertThat(list).as("List of stops filtered by county").hasSize(1);

        list = zoneCountyFilterer.filterByCountyMatch(Arrays.asList(county1.getNetexId()), Arrays.asList(stopPlace), true);

        assertThat(list).as("Negated list of stops not in county").isEmpty();

    }

}