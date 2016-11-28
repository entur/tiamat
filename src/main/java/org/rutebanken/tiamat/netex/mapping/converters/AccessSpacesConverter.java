package org.rutebanken.tiamat.netex.mapping.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.AccessSpaces_RelStructure;
import org.rutebanken.tiamat.model.AccessSpace;

import java.util.List;

public class AccessSpacesConverter extends BidirectionalConverter<List<AccessSpace>, AccessSpaces_RelStructure> {
    @Override
    public AccessSpaces_RelStructure convertTo(List<AccessSpace> accessSpaces, Type<AccessSpaces_RelStructure> type) {
        return null;
    }

    @Override
    public List<AccessSpace> convertFrom(AccessSpaces_RelStructure accessSpaces_relStructure, Type<List<AccessSpace>> type) {
        return null;
    }
}
