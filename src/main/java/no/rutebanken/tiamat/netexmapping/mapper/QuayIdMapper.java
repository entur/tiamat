package no.rutebanken.tiamat.netexmapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import no.rutebanken.tiamat.model.Quay;
import no.rutebanken.tiamat.netexmapping.NetexIdMapper;

public class QuayIdMapper extends CustomMapper<no.rutebanken.netex.model.Quay, Quay> {

    private NetexIdMapper netexIdMapper = new NetexIdMapper();

    @Override
    public void mapAtoB(no.rutebanken.netex.model.Quay netexQuay, no.rutebanken.tiamat.model.Quay internalQuay, MappingContext context) {
        netexIdMapper.toTiamatModel(netexQuay, internalQuay);
    }

    @Override
    public void mapBtoA(no.rutebanken.tiamat.model.Quay tiamatQuay, no.rutebanken.netex.model.Quay netexQuay, MappingContext context) {
        netexIdMapper.toNetexModel(tiamatQuay, netexQuay);
    }
}
