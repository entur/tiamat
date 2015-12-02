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

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = TiamatApplication.class)
@ActiveProfiles("dev")
public class GtfsIfoptMapperTest {

    @Autowired
    private GtfsIfoptMapper gtfsIfoptMapper;

    @Test
    public void mappingStopLatitudeToCentroid() throws Exception {

        BigDecimal latitude = new BigDecimal("59.9116770");

        Stop stop = new Stop();
        stop.setLat(latitude.doubleValue());

        StopPlace stopPlace = gtfsIfoptMapper.centroid(new StopPlace(), stop);

        LocationStructure locationStructure = stopPlace.getCentroid().getLocation();

        assertThat(locationStructure.getLatitude().compareTo(latitude)).isEqualTo(0);

    }

    @Test
    public void mappingStopLongitudeToCentroid() throws Exception {

        BigDecimal longitude = new BigDecimal("10.758853");

        Stop stop = new Stop();
        stop.setLon(longitude.doubleValue());

        StopPlace stopPlace = gtfsIfoptMapper.centroid(new StopPlace(), stop);

        LocationStructure locationStructure = stopPlace.getCentroid().getLocation();

        assertThat(locationStructure.getLongitude()).isEqualTo(longitude);
    }
}