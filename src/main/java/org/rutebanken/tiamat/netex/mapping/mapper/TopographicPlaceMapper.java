package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.TopographicPlace;
import org.rutebanken.netex.model.TopographicPlaceDescriptor_VersionedChildStructure;
import org.rutebanken.tiamat.netex.mapping.NetexIdMapper;

public class TopographicPlaceMapper extends CustomMapper<TopographicPlace, org.rutebanken.tiamat.model.TopographicPlace> {

    private NetexIdMapper netexIdMapper = new NetexIdMapper();

    @Override
    public void mapAtoB(TopographicPlace netexTopographicPlace, org.rutebanken.tiamat.model.TopographicPlace tiamatTopographicPlace, MappingContext context) {
        super.mapAtoB(netexTopographicPlace, tiamatTopographicPlace, context);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.TopographicPlace tiamatTopographicPlace, TopographicPlace netexTopographicPlace, MappingContext context) {
        super.mapBtoA(tiamatTopographicPlace, netexTopographicPlace, context);
        netexTopographicPlace.withDescriptor(
                new TopographicPlaceDescriptor_VersionedChildStructure()
                        .withName(new MultilingualString().withValue(tiamatTopographicPlace.getName().getValue())));
        netexTopographicPlace.setVersion("any");

    }
}
