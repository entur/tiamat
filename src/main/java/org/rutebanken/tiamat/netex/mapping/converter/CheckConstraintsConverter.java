package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.CheckConstraints_RelStructure;
import org.rutebanken.tiamat.model.CheckConstraint;

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
