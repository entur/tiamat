package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;

public class GroupOfStopPlacesMapper extends CustomMapper<GroupOfStopPlaces, org.rutebanken.tiamat.model.GroupOfStopPlaces> {

    @Override
    public void mapAtoB(GroupOfStopPlaces netexGroupOfStopPlaces, org.rutebanken.tiamat.model.GroupOfStopPlaces tiamatGroupOfStopPlaces, MappingContext context) {
        super.mapAtoB(netexGroupOfStopPlaces, tiamatGroupOfStopPlaces, context);
        final EmbeddableMultilingualString name = tiamatGroupOfStopPlaces.getPurposeOfGrouping().getName();
        if (name != null) {
            netexGroupOfStopPlaces.getPurposeOfGroupingRef().withRef("NSR:PurposeOfGrouping:"+name.getValue());
        }

    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.GroupOfStopPlaces tiamatGroupOfStopPlaces, GroupOfStopPlaces netexGroupOfStopPlaces, MappingContext context) {
        super.mapBtoA(tiamatGroupOfStopPlaces, netexGroupOfStopPlaces, context);
        //todo implement netex to tiamat mappring
    }
}
