package org.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.ValidityConditions_RelStructure;
import org.rutebanken.tiamat.model.ValidityCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ValidityConditionsConverter extends BidirectionalConverter<List<ValidityCondition>, ValidityConditions_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(ValidityConditionsConverter.class);

    @Override
    public ValidityConditions_RelStructure convertTo(List<ValidityCondition> validityConditions, Type<ValidityConditions_RelStructure> type) {

        return null;
//        if(validityConditions.isEmpty()) return null;
//        ValidityConditions_RelStructure validityConditions_relStructure = new org.rutebanken.netex.model.ValidityConditions_RelStructure();
//
//        validityConditions.forEach(validityCondition ->
//                validityConditions_relStructure.getValidityConditionRefOrValidBetweenOrValidityCondition_().add(
//                        mapperFacade.map(validityCondition, org.rutebanken.netex.model.ValidityCondition.class)
//                ));
//
//        return validityConditions_relStructure;
    }

    @Override
    public List<ValidityCondition> convertFrom(ValidityConditions_RelStructure validityConditions_relStructure, Type<List<ValidityCondition>> type) {
        return null;
    }
}
