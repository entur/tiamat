package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.SpotRow;

public class SpotRowMapper extends CustomMapper<SpotRow, org.rutebanken.tiamat.model.vehicle.SpotRow> {

    @Override
    public void mapAtoB(SpotRow netexSpotRow, org.rutebanken.tiamat.model.vehicle.SpotRow tiamatSpotRow, MappingContext context) {
        super.mapAtoB(netexSpotRow, tiamatSpotRow, context);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.vehicle.SpotRow tiamatSpotRow, SpotRow netexSpotRow, MappingContext context) {
        super.mapBtoA(tiamatSpotRow, netexSpotRow, context);

        if (tiamatSpotRow.getLabel() != null) {
            netexSpotRow.getLabel().withContent(tiamatSpotRow.getLabel().getValue());
        }
    }
}
