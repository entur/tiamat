package org.rutebanken.tiamat.rest.graphql.mappers;


import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class ValidBetweenMapper {

    private final IdMapper idMapper;

    @Autowired
    public ValidBetweenMapper(IdMapper idMapper) {
        this.idMapper = idMapper;
    }

    public ValidBetween map(Map input) {

        if(input == null) {
            return null;
        }

        ValidBetween validBetween = new ValidBetween();

        if(input.get(VALID_BETWEEN_FROM_DATE) != null) {
            validBetween.setFromDate((Instant) input.get(VALID_BETWEEN_FROM_DATE));
        }

        if(input.get(VALID_BETWEEN_TO_DATE) != null) {
            validBetween.setToDate((Instant) input.get(VALID_BETWEEN_TO_DATE));
        }

        return validBetween;
    }
}
