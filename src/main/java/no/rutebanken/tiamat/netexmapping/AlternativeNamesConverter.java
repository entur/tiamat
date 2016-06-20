package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.netex.model.AlternativeNames_RelStructure;
import no.rutebanken.tiamat.model.AlternativeName;

import java.util.List;

public class AlternativeNamesConverter extends CustomConverter<List<AlternativeName>, AlternativeNames_RelStructure> {
    @Override
    public AlternativeNames_RelStructure convert(List<AlternativeName> alternativeNames, Type<? extends AlternativeNames_RelStructure> type) {
        return null;
    }

}
