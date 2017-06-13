package org.rutebanken.tiamat.rest.graphql.resolver;


import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class ValidBetweenMapper {

    private final IdResolver idResolver;

    @Autowired
    public ValidBetweenMapper(IdResolver idResolver) {
        this.idResolver = idResolver;
    }

    public ValidBetween map(Map input) {
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
