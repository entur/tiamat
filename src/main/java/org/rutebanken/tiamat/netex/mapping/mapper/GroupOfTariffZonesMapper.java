package org.rutebanken.tiamat.netex.mapping.mapper;

import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.TariffZoneRef;

import java.util.List;

public class GroupOfTariffZonesMapper extends CustomMapper<GroupOfTariffZones, org.rutebanken.tiamat.model.GroupOfTariffZones> {

    @Override
    public void mapAtoB(GroupOfTariffZones netexGroupOfTariffZones, org.rutebanken.tiamat.model.GroupOfTariffZones tiamatGroupOfTariffZones, MappingContext context) {
        super.mapAtoB(netexGroupOfTariffZones, tiamatGroupOfTariffZones, context);
        if (netexGroupOfTariffZones.getMembers() != null && !netexGroupOfTariffZones.getMembers().getTariffZoneRef_().isEmpty()) {
            final List<TariffZoneRef> tiamatTariffZoneRefList = netexGroupOfTariffZones.getMembers().getTariffZoneRef_().stream()
                    .map(JAXBElement::getValue)
                    .map(tzr -> new TariffZoneRef(tzr.getRef())).toList();

            tiamatGroupOfTariffZones.getMembers().addAll(tiamatTariffZoneRefList);
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.GroupOfTariffZones tiamatGroupOfTariffZones, GroupOfTariffZones netexGroupOfTariffZones, MappingContext context) {
        super.mapBtoA(tiamatGroupOfTariffZones, netexGroupOfTariffZones, context);
    }
}
