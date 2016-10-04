package no.rutebanken.tiamat.netexmapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import no.rutebanken.netex.model.StopPlace;
import org.jboss.logging.Logger;

public class StopPlaceIdMapper extends CustomMapper<StopPlace, no.rutebanken.tiamat.model.StopPlace> {

    private static final Logger LOGGER = Logger.getLogger(StopPlaceIdMapper.class);

    @Override
    public void mapAtoB(StopPlace netexStopPlace, no.rutebanken.tiamat.model.StopPlace tiamatStopPlace, MappingContext
            context) {
        if(netexStopPlace.getId() == null) {
            tiamatStopPlace.setId(null);
        } else {
            String netexId = netexStopPlace.getId();
            Long tiamatId = Long.valueOf(netexId.substring(netexId.lastIndexOf(':') + 1));
            tiamatStopPlace.setId(tiamatId);
        }
    }

    @Override
    public void mapBtoA(no.rutebanken.tiamat.model.StopPlace tiamatStopPlace, StopPlace netexStopPlace, MappingContext context) {

        if(tiamatStopPlace.getId() == null) {
            LOGGER.warn("Id for internal model is null. Mapping to null value.");
            netexStopPlace.setId(null);
        } else {
            netexStopPlace.setId("NSR:" + "StopPlace:" + tiamatStopPlace.getId().toString());
        }
    }
}

