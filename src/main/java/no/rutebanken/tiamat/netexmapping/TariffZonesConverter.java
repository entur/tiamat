package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.netex.model.TariffZoneRefs_RelStructure;
import no.rutebanken.tiamat.model.TariffZone;

import java.util.List;

public class TariffZonesConverter extends CustomConverter<List<TariffZone>, TariffZoneRefs_RelStructure> {
    @Override
    public TariffZoneRefs_RelStructure convert(List<TariffZone> levels, Type<? extends TariffZoneRefs_RelStructure> type) {
        return null;
    }
}
