package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.tiamat.model.EquipmentPlace;
import no.rutebanken.netex.model.EquipmentPlaces_RelStructure;

import java.util.List;

public class EquipmentPlacesConverter extends CustomConverter<List<EquipmentPlace>, EquipmentPlaces_RelStructure> {
    @Override
    public EquipmentPlaces_RelStructure convert(List<EquipmentPlace> equipmentPlaces, Type<? extends EquipmentPlaces_RelStructure> type) {
        return null;
    }

}
