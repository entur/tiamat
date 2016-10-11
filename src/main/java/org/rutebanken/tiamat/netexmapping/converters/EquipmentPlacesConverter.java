package org.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.tiamat.model.EquipmentPlace;
import org.rutebanken.netex.model.EquipmentPlaces_RelStructure;

import java.util.List;

public class EquipmentPlacesConverter extends BidirectionalConverter<List<EquipmentPlace>, EquipmentPlaces_RelStructure> {

    @Override
    public EquipmentPlaces_RelStructure convertTo(List<EquipmentPlace> equipmentPlaces, Type<EquipmentPlaces_RelStructure> type) {
        return null;
    }

    @Override
    public List<EquipmentPlace> convertFrom(EquipmentPlaces_RelStructure equipmentPlaces_relStructure, Type<List<EquipmentPlace>> type) {
        return null;
    }
}
