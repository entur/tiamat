package no.rutebanken.tiamat.gtfs;

import com.vividsolutions.jts.geom.Point;
import no.rutebanken.tiamat.TiamatApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onebusaway.gtfs.model.Stop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.org.netex.netex.StopPlace;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
public class GtfsIfoptMapperTest {

    @Autowired
    private GtfsIfoptMapper gtfsIfoptMapper;

    @Test
    public void mapStopLatitudeToCentroid() throws Exception {

        double gtfsLatitude = 59.911677022;

        Stop stop = new Stop();
        stop.setLat(gtfsLatitude);

        StopPlace stopPlace = gtfsIfoptMapper.centroid(new StopPlace(), stop);

        Point locationStructure = stopPlace.getCentroid().getLocation();

        assertThat(locationStructure.getY()).isEqualTo(gtfsLatitude);

    }

    @Test
    public void mapLongitudeToCentroid() throws Exception {

        double gtfsLongitude = 10.758853;

        Stop stop = new Stop();
        stop.setLon(gtfsLongitude);

        StopPlace stopPlace = gtfsIfoptMapper.centroid(new StopPlace(), stop);

        Point locationStructure = stopPlace.getCentroid().getLocation();

        assertThat(locationStructure.getX()).isEqualTo(gtfsLongitude);
    }
}