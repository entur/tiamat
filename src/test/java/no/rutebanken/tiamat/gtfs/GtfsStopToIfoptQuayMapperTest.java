package no.rutebanken.tiamat.gtfs;

import com.vividsolutions.jts.geom.Point;
import no.rutebanken.tiamat.TiamatApplication;
import no.rutebanken.tiamat.model.Quay;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onebusaway.gtfs.model.Stop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
public class GtfsStopToIfoptQuayMapperTest {

    @Autowired
    private GtfsStopToIfoptQuayMapper stopToQuayMapper;

    @Test
    public void mapStopLatitudeToCentroid() throws Exception {

        double gtfsLatitude = 59.911677022;

        Stop stop = new Stop();
        stop.setLat(gtfsLatitude);

        Quay quay = new Quay();
        stopToQuayMapper.centroid(quay, stop);

        Point locationStructure = quay.getCentroid().getLocation().getGeometryPoint();

        assertThat(locationStructure.getY()).isEqualTo(gtfsLatitude);

    }

    @Test
    public void mapLongitudeToCentroid() throws Exception {

        double gtfsLongitude = 10.758853;

        Stop stop = new Stop();
        stop.setLon(gtfsLongitude);

        Quay quay = new Quay();
        stopToQuayMapper.centroid(quay, stop);

        Point locationStructure = quay.getCentroid().getLocation().getGeometryPoint();

        assertThat(locationStructure.getX()).isEqualTo(gtfsLongitude);
    }
}