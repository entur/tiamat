package org.rutebanken.tiamat.netexmapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.netexmapping.NetexIdMapper;

public class StopPlaceIdMapper extends CustomMapper<StopPlace, org.rutebanken.tiamat.model.StopPlace> {

    private NetexIdMapper netexIdMapper = new NetexIdMapper();

    @Override
    public void mapAtoB(StopPlace netexStopPlace, org.rutebanken.tiamat.model.StopPlace tiamatStopPlace, MappingContext
            context) {
        netexIdMapper.toTiamatModel(netexStopPlace, tiamatStopPlace);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.StopPlace tiamatStopPlace, StopPlace netexStopPlace, MappingContext context) {
        netexIdMapper.toNetexModel(tiamatStopPlace, netexStopPlace);
    }
}

