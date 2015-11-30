package no.rutebanken.tiamat.gtfs;

import org.onebusaway.gtfs.model.Stop;
import org.springframework.stereotype.Component;
import uk.org.netex.netex.MultilingualString;
import uk.org.netex.netex.StopPlace;

@Component
public class GtfsIfoptMapper {

    public StopPlace map(Stop stop) {

        StopPlace stopPlace = new StopPlace();
        stopPlace.setName(new MultilingualString(stop.getName(), "no", ""));
        stopPlace.setDescription(new MultilingualString(stop.getDesc(), "no", ""));

        return stopPlace;
    }
}

