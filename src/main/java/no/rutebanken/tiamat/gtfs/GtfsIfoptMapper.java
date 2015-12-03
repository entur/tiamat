package no.rutebanken.tiamat.gtfs;

import org.onebusaway.gtfs.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.Location;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.SimplePoint_VersionStructure;
import uk.org.netex.netex.StopPlace;

import java.math.BigDecimal;

@Component
public class GtfsIfoptMapper {

    private static final Logger logger = LoggerFactory.getLogger(GtfsIfoptMapper.class);

    private static final int DECIMAL_PLACES = 8;

    public StopPlace map(Stop stop) {

        logger.debug("Mapping data from GTFS stop to IFOPT stop place {} - {}", stop.getId(), stop.getName());

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString(stop.getName(), "no", ""));
        stopPlace.setDescription(new MultilingualString(stop.getDesc(), "no", ""));

        stopPlace = centroid(stopPlace, stop);

        return stopPlace;
    }

    public StopPlace centroid(StopPlace stopPlace, Stop stop) {
        SimplePoint_VersionStructure centroid = new SimplePoint_VersionStructure();

        logger.trace("Setting location on stop place {} latitude: {}, longitude: {}", stopPlace.getName(), stop.getLat(), stop.getLon());

        Location location = new Location();
        location.setLatitude(coordinateFromDouble(stop.getLat()));
        location.setLongitude(coordinateFromDouble(stop.getLon()));

        centroid.setLocation(location);

        stopPlace.setCentroid(centroid);
        return stopPlace;
    }

    public BigDecimal coordinateFromDouble(double coordinate) {
        BigDecimal result = new BigDecimal(String.valueOf(coordinate)).setScale(DECIMAL_PLACES, BigDecimal.ROUND_HALF_UP);
        logger.trace("Coordinate value from double value: {} to BigDecimal value: {}", coordinate, result.toPlainString());
        return result;
    }

}

