package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.tiamat.model.InstalledEquipment_VersionStructure;

public class InstalledEquipmentConverter extends BidirectionalConverter<InstalledEquipment_VersionStructure, org.rutebanken.netex.model.InstalledEquipment_VersionStructure> {

    @Override
    public org.rutebanken.netex.model.InstalledEquipment_VersionStructure convertTo(InstalledEquipment_VersionStructure sanitaryEquipment, Type<org.rutebanken.netex.model.InstalledEquipment_VersionStructure> type) {
        return null;
    }

    @Override
    public InstalledEquipment_VersionStructure convertFrom(org.rutebanken.netex.model.InstalledEquipment_VersionStructure sanitaryEquipment, Type<InstalledEquipment_VersionStructure> type) {
        return null;
    }
}
