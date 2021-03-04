package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.AuthorityRefStructure;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZoneRefStructure;
import org.rutebanken.netex.model.FareZoneRefs_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.tiamat.model.TariffZoneRef;

import javax.xml.bind.JAXBElement;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class FareZoneMapper extends CustomMapper<FareZone, org.rutebanken.tiamat.model.FareZone> {

    final ObjectFactory objectFactory = new ObjectFactory();

    @Override
    public void mapAtoB(FareZone netexFareZone, org.rutebanken.tiamat.model.FareZone tiamatFareZone, MappingContext context) {
        super.mapAtoB(netexFareZone, tiamatFareZone, context);
        if (netexFareZone.getTransportOrganisationRef() != null && netexFareZone.getTransportOrganisationRef().getValue() != null) {
            tiamatFareZone.setTransportOrganisationRef(netexFareZone.getTransportOrganisationRef().getValue().getRef());
        }
        if (netexFareZone.getNeighbours() != null && !netexFareZone.getNeighbours().getFareZoneRef().isEmpty()) {
            Set<TariffZoneRef> tiamatNeighbours = new HashSet<>();
            final List<FareZoneRefStructure> fareZoneRef = netexFareZone.getNeighbours().getFareZoneRef();
            for (FareZoneRefStructure fareZoneRefStructure : fareZoneRef) {
                tiamatNeighbours.add(new TariffZoneRef(fareZoneRefStructure.getRef()));
            }
            tiamatFareZone.setNeighbours(tiamatNeighbours);
        }
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.FareZone tiamatFareZone, FareZone netexFareZone, MappingContext context) {
        super.mapBtoA(tiamatFareZone, netexFareZone, context);
        if (tiamatFareZone.getTransportOrganisationRef() != null) {
            final JAXBElement<AuthorityRefStructure> authorityRef = objectFactory.createAuthorityRef(new AuthorityRefStructure().withRef(tiamatFareZone.getTransportOrganisationRef()));
            netexFareZone.withTransportOrganisationRef(authorityRef);
        }

        if (!tiamatFareZone.getNeighbours().isEmpty()) {
            final List<FareZoneRefStructure> fareZoneRefs = tiamatFareZone.getNeighbours().stream()
                    .map(tariffZoneRef -> new FareZoneRefStructure().withRef(tariffZoneRef.getRef()))
                    .collect(Collectors.toList());
            final FareZoneRefs_RelStructure fareZoneRefsRelStructure = new FareZoneRefs_RelStructure().withFareZoneRef(fareZoneRefs);
            netexFareZone.withNeighbours(fareZoneRefsRelStructure);
        }


    }
}
