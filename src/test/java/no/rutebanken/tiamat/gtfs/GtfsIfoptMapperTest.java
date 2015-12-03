package no.rutebanken.tiamat.gtfs;

import no.rutebanken.tiamat.TiamatApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.onebusaway.gtfs.model.Stop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.org.netex.netex.LocationStructure;
import uk.org.netex.netex.StopPlace;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
@ActiveProfiles("dev")
public class GtfsIfoptMapperTest {

    @Autowired
    private GtfsIfoptMapper gtfsIfoptMapper;

    @Test
    public void mapStopLatitudeToCentroidExpectEightDecimalPlaces() throws Exception {

        double gtfsLatitude = 59.911677022;

        Stop stop = new Stop();
        stop.setLat(gtfsLatitude);

        StopPlace stopPlace = gtfsIfoptMapper.centroid(new StopPlace(), stop);

        LocationStructure locationStructure = stopPlace.getCentroid().getLocation();

        assertThat(locationStructure.getLatitude().toPlainString()).isEqualTo("59.91167702");

    }

    @Test
    public void mapLongitudeToCentroidExpectEightDecimalPlaces() throws Exception {

        double gtfsLongitude = 10.758853;

        Stop stop = new Stop();
        stop.setLon(gtfsLongitude);

        StopPlace stopPlace = gtfsIfoptMapper.centroid(new StopPlace(), stop);

        LocationStructure locationStructure = stopPlace.getCentroid().getLocation();

        assertThat(locationStructure.getLongitude()).isEqualTo("10.75885300");
    }
}