package no.rutebanken.tiamat.netexmapping;

import ma.glasnost.orika.CustomConverter;
import ma.glasnost.orika.metadata.Type;
import no.rutebanken.netex.model.ValidityConditions_RelStructure;
import no.rutebanken.tiamat.model.ValidityCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class ValidityConditionsConverter extends CustomConverter<List<no.rutebanken.tiamat.model.ValidityCondition>, ValidityConditions_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(ValidityConditionsConverter.class);

    @Override
    public ValidityConditions_RelStructure convert(List<ValidityCondition> validityConditions, Type<? extends ValidityConditions_RelStructure> type) {
        ValidityConditions_RelStructure validityConditions_relStructure = new no.rutebanken.netex.model.ValidityConditions_RelStructure();

        validityConditions.forEach(validityCondition ->
                validityConditions_relStructure.getValidityConditionRefOrValidBetweenOrValidityCondition_().add(
                mapperFacade.map(validityCondition, no.rutebanken.netex.model.ValidityCondition.class)
        ));

        return validityConditions_relStructure;
    }

}
