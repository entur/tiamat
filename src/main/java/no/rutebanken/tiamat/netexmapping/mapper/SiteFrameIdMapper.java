package no.rutebanken.tiamat.netexmapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.SiteFrame;
import no.rutebanken.tiamat.netexmapping.NetexIdMapper;

public class SiteFrameIdMapper extends CustomMapper<SiteFrame, no.rutebanken.tiamat.model.SiteFrame> {

    private NetexIdMapper netexIdMapper = new NetexIdMapper();

    @Override
    public void mapAtoB(SiteFrame netexSiteFrame, no.rutebanken.tiamat.model.SiteFrame tiamatModel, MappingContext
            context) {
        netexIdMapper.toTiamatModel(netexSiteFrame, tiamatModel);
    }

    @Override
    public void mapBtoA(no.rutebanken.tiamat.model.SiteFrame tiamatSiteFrame, SiteFrame netexSiteFrame, MappingContext context) {
        netexIdMapper.toNetexModel(tiamatSiteFrame, netexSiteFrame);
    }
}

