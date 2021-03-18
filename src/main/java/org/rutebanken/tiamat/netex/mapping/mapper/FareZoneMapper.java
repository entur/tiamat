package org.rutebanken.tiamat.netex.mapping.mapper;

import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.apache.commons.lang3.StringUtils;
import org.rutebanken.netex.model.AuthorityRefStructure;
import org.rutebanken.netex.model.FareZone;
import org.rutebanken.netex.model.FareZoneRefStructure;
import org.rutebanken.netex.model.FareZoneRefs_RelStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PointRefStructure;
import org.rutebanken.netex.model.PointRefs_RelStructure;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.ScopingMethodEnumeration;
import org.rutebanken.tiamat.model.StopPlaceReference;
import org.rutebanken.tiamat.model.TariffZoneRef;
import javax.xml.bind.JAXBElement;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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

        if (netexFareZone.getScopingMethod() != null && netexFareZone.getScopingMethod().equals(ScopingMethodEnumeration.EXPLICIT_STOPS)
                && netexFareZone.getMembers() != null && !netexFareZone.getMembers().getPointRef().isEmpty()) {

            var fareZoneMembers = netexFareZone.getMembers().getPointRef().stream()
                    .map(jaxbElement -> new StopPlaceReference(convertScheduledStopPointRefToStopPlaceRef(jaxbElement.getValue().getRef())))
                    .collect(Collectors.toSet());

            tiamatFareZone.setFareZoneMembers(fareZoneMembers);
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

        if (!tiamatFareZone.getFareZoneMembers().isEmpty()) {
            List<JAXBElement<? extends PointRefStructure>> fareZoneMember = tiamatFareZone.getFareZoneMembers().stream()
                    .map(members -> new ObjectFactory().createScheduledStopPointRef(new ScheduledStopPointRefStructure().withRef(convertStopPlaceRefToScheduledStopPointRef(members.getRef()))))
                    .collect(Collectors.toList());

            PointRefs_RelStructure pointRefsRelStructure = new PointRefs_RelStructure().withPointRef(fareZoneMember);
            netexFareZone.withMembers(pointRefsRelStructure);
        }

    }

    private String convertStopPlaceRefToScheduledStopPointRef(String netexId) {
        validateNetexId(netexId);
        var idPrefix = netexId.substring(0, netexId.indexOf(':'));
        var id= netexId.substring(netexId.lastIndexOf(':') + 1).trim();
        return idPrefix + ":ScheduledStopPoint:S" + id;
    }

    private String convertScheduledStopPointRefToStopPlaceRef(String netexId) {
        validateNetexId(netexId);
        var idPrefix = netexId.substring(0, netexId.indexOf(':'));
        var id= netexId.substring(netexId.lastIndexOf(':') + 1).trim().substring(1);
        return idPrefix + ":StopPlace:" + id;

    }

    private void validateNetexId(String netexId) {
        Objects.requireNonNull(netexId);
        if(StringUtils.countMatches(netexId, ":") != 2) {
            throw new IllegalArgumentException("Number of colons in ID is not two: " + netexId);
        }
    }
}
