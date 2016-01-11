package no.rutebanken.tiamat.gtfs;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import org.onebusaway.gtfs.model.Stop;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.Quay;
import uk.org.netex.netex.SimplePoint;
import uk.org.netex.netex.Site_VersionStructure;
import uk.org.netex.netex.StopPlace;
import uk.org.netex.netex.Zone_VersionStructure;

@Component
public class GtfsIfoptMapper {

    private static final Logger logger = LoggerFactory.getLogger(GtfsIfoptMapper.class);

    private static final int DECIMAL_PLACES = 8;

    @Autowired
    private GeometryFactory geometryFactory;

    public StopPlace map(Stop stop) {

        logger.debug("Mapping data from GTFS stop to IFOPT stop place {} - {}", stop.getId(), stop.getName());

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString(stop.getName(), "no", ""));
        stopPlace.setDescription(new MultilingualString(stop.getDesc(), "no", ""));

        centroid(stopPlace, stop);

        return stopPlace;
    }
    
    public Quay mapToQuay(Stop stop){
    	logger.debug("Mapping data from GTFS stop to IFOPT quay {} - {}", stop.getId(), stop.getName());
    	Quay quay = new Quay();
    	quay.setName(new MultilingualString(stop.getName(), "no", ""));
    	quay.setDescription(new MultilingualString(stop.getDesc(), "no", ""));
    	
    	centroid(quay, stop);
    	
    	return quay;
    }

    public void centroid(Zone_VersionStructure site, Stop stop) {
        SimplePoint centroid = new SimplePoint();

        logger.trace("Setting location for {} latitude: {}, longitude: {}", site.getName(), stop.getLat(), stop.getLon());

        centroid.setLocation(geometryFactory.createPoint(new Coordinate(stop.getLon(), stop.getLat())));

        site.setCentroid(centroid);
        //return site;
    }
}

