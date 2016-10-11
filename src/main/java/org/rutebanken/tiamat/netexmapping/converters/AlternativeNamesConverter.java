package org.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.AlternativeNames_RelStructure;
import org.rutebanken.tiamat.model.AlternativeName;

import java.util.List;

public class AlternativeNamesConverter extends BidirectionalConverter<List<AlternativeName>, AlternativeNames_RelStructure> {
    @Override
    public AlternativeNames_RelStructure convertTo(List<AlternativeName> alternativeNames, Type<AlternativeNames_RelStructure> type) {
        return null;
    }

    @Override
    public List<AlternativeName> convertFrom(AlternativeNames_RelStructure alternativeNames_relStructure, Type<List<AlternativeName>> type) {
        return null;
    }
}
