package no.rutebanken.tiamat.netexmapping.converters;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.netex.model.ValidityConditions_RelStructure;
import no.rutebanken.tiamat.model.ValidityCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ValidityConditionsConverter extends BidirectionalConverter<List<ValidityCondition>, ValidityConditions_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(ValidityConditionsConverter.class);

    @Override
    public ValidityConditions_RelStructure convertTo(List<ValidityCondition> validityConditions, Type<ValidityConditions_RelStructure> type) {

        if(validityConditions.isEmpty()) return null;
        ValidityConditions_RelStructure validityConditions_relStructure = new no.rutebanken.netex.model.ValidityConditions_RelStructure();

        validityConditions.forEach(validityCondition ->
                validityConditions_relStructure.getValidityConditionRefOrValidBetweenOrValidityCondition_().add(
                        mapperFacade.map(validityCondition, no.rutebanken.netex.model.ValidityCondition.class)
                ));

        return validityConditions_relStructure;
    }

    @Override
    public List<ValidityCondition> convertFrom(ValidityConditions_RelStructure validityConditions_relStructure, Type<List<ValidityCondition>> type) {
        return null;
    }
}
