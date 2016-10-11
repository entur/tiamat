package no.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import no.rutebanken.tiamat.model.TariffZone;

import java.util.List;

public class TariffZonesConverter extends BidirectionalConverter<List<TariffZone>, TariffZoneRefs_RelStructure> {
    @Override
    public TariffZoneRefs_RelStructure convertTo(List<TariffZone> tariffZones, Type<TariffZoneRefs_RelStructure> type) {
        return null;
    }

    @Override
    public List<TariffZone> convertFrom(TariffZoneRefs_RelStructure tariffZoneRefs_relStructure, Type<List<TariffZone>> type) {
        return null;
    }
}
