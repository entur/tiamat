package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.netex.model.AccessSpaces_RelStructure;
import no.rutebanken.tiamat.model.AccessSpace;

import java.util.ArrayList;

public class AccessSpacesConverter extends CustomConverter<ArrayList<AccessSpace>, AccessSpaces_RelStructure> {
    @Override
    public no.rutebanken.netex.model.AccessSpaces_RelStructure convert(ArrayList<AccessSpace> accessSpaces, Type<? extends AccessSpaces_RelStructure> type) {
        return null;
    }

}
