package no.rutebanken.tiamat.netexmapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import no.rutebanken.netex.model.StopPlace;
import no.rutebanken.tiamat.netexmapping.NetexIdMapper;

public class StopPlaceIdMapper extends CustomMapper<StopPlace, no.rutebanken.tiamat.model.StopPlace> {

    private NetexIdMapper netexIdMapper = new NetexIdMapper();

    @Override
    public void mapAtoB(StopPlace netexStopPlace, no.rutebanken.tiamat.model.StopPlace tiamatStopPlace, MappingContext
            context) {
        netexIdMapper.toTiamatModel(netexStopPlace, tiamatStopPlace);
    }

    @Override
    public void mapBtoA(no.rutebanken.tiamat.model.StopPlace tiamatStopPlace, StopPlace netexStopPlace, MappingContext context) {
        netexIdMapper.toNetexModel(tiamatStopPlace, netexStopPlace);
    }
}

