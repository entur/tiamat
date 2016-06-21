package no.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.netex.model.BoardingPositions_RelStructure;
import no.rutebanken.netex.model.CheckConstraints_RelStructure;
import no.rutebanken.tiamat.model.BoardingPosition;
import no.rutebanken.tiamat.model.CheckConstraint;

import java.util.List;

public class CheckConstraintsConverter extends BidirectionalConverter<List<CheckConstraint>, CheckConstraints_RelStructure> {

    @Override
    public CheckConstraints_RelStructure convertTo(List<CheckConstraint> checkConstraints, Type<CheckConstraints_RelStructure> type) {
        return null;
    }

    @Override
    public List<CheckConstraint> convertFrom(CheckConstraints_RelStructure checkConstraints_relStructure, Type<List<CheckConstraint>> type) {
        return null;
    }
}
