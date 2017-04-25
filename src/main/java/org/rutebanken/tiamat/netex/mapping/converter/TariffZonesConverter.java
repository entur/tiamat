package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.model.TariffZoneRef;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
public class TariffZonesConverter extends BidirectionalConverter<Set<TariffZoneRef>, TariffZoneRefs_RelStructure> {
    @Override
    public TariffZoneRefs_RelStructure convertTo(Set<TariffZoneRef> tariffZones, Type<TariffZoneRefs_RelStructure> type) {
        return null;
    }

    @Override
    public Set<TariffZoneRef> convertFrom(TariffZoneRefs_RelStructure tariffZoneRefs_relStructure, Type<Set<TariffZoneRef>> type) {
        return null;
    }
}

