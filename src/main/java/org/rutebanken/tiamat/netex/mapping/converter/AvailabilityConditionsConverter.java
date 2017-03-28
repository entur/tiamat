package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.AvailabilityConditions_RelStructure;
import org.rutebanken.netex.model.ValidityConditions_RelStructure;
import org.rutebanken.tiamat.model.AvailabilityCondition;
import org.rutebanken.netex.model.ValidBetween;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AvailabilityConditionsConverter extends BidirectionalConverter<List<AvailabilityCondition>, ValidityConditions_RelStructure> {

    private static final Logger logger = LoggerFactory.getLogger(AvailabilityConditionsConverter.class);

     @Override
    public ValidityConditions_RelStructure convertTo(List<AvailabilityCondition> availabilityConditions, Type<ValidityConditions_RelStructure> type) {
        if(availabilityConditions == null || availabilityConditions.isEmpty()) {
            return null;
        }

        logger.debug("Mapping availabilityConditions {}", availabilityConditions);

        return new ValidityConditions_RelStructure()
                .withValidityConditionRefOrValidBetweenOrValidityCondition_(
                        availabilityConditions.stream()
                        .map(tiamatAvailabilityCondition -> {
                            ValidBetween validBetween = new ValidBetween();

                            if(tiamatAvailabilityCondition.getFromDate() != null) {
                                validBetween.setFromDate(tiamatAvailabilityCondition.getFromDate().toOffsetDateTime());
                            }
                            if(tiamatAvailabilityCondition.getToDate() != null) {
                                validBetween.setToDate(tiamatAvailabilityCondition.getToDate().toOffsetDateTime());
                            }

                            return validBetween;
                        })
                        .collect(Collectors.toList()));
    }

    @Override
    public List<AvailabilityCondition> convertFrom(ValidityConditions_RelStructure validityConditionsRelStructure, Type<List<AvailabilityCondition>> type) {
        logger.debug("Ignoring incoming availability conditions {}", validityConditionsRelStructure);
        return null;
    }
}
