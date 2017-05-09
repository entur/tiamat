package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import org.rutebanken.netex.model.TariffZone_VersionStructure;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

import static java.util.stream.Collectors.toCollection;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Component
public class TariffZonesRefConverter extends BidirectionalConverter<Set<TariffZoneRef>, TariffZoneRefs_RelStructure> {
    @Override
    public TariffZoneRefs_RelStructure convertTo(Set<TariffZoneRef> tariffZones, Type<TariffZoneRefs_RelStructure> type) {

        if(tariffZones == null || tariffZones.isEmpty()) {
            return null;
        }

        return new TariffZoneRefs_RelStructure()
                .withTariffZoneRef(tariffZones.stream()
                    .map(tariffZoneRef -> mapperFacade.map(tariffZoneRef, org.rutebanken.netex.model.TariffZoneRef.class))
                        .collect(toList()));
    }

    @Override
    public Set<TariffZoneRef> convertFrom(TariffZoneRefs_RelStructure tariffZoneRefs_relStructure, Type<Set<TariffZoneRef>> type) {
        if(tariffZoneRefs_relStructure == null
                || tariffZoneRefs_relStructure.getTariffZoneRef() == null
                || tariffZoneRefs_relStructure.getTariffZoneRef().isEmpty()) {
            return null;
        }

        return tariffZoneRefs_relStructure
                .getTariffZoneRef()
                .stream()
                .map(tariffZoneRef -> mapperFacade.map(tariffZoneRef, TariffZoneRef.class))
                .collect(toSet());

    }
}

