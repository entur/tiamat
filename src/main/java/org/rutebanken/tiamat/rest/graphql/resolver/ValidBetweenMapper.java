package org.rutebanken.tiamat.rest.graphql.resolver;


import org.rutebanken.tiamat.model.ValidBetween;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN_FROM_DATE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN_TO_DATE;

@Component
public class ValidBetweenMapper {

    private final IdResolver idResolver;

    @Autowired
    public ValidBetweenMapper(IdResolver idResolver) {
        this.idResolver = idResolver;
    }

    public ValidBetween map(Map input) {
        ValidBetween validBetween = new ValidBetween();

        idResolver.extractAndSetNetexId(ID, input, validBetween);

        if(input.get(VALID_BETWEEN_FROM_DATE) != null) {
            validBetween.setFromDate((ZonedDateTime) input.get(VALID_BETWEEN_FROM_DATE));
        }

        if(input.get(VALID_BETWEEN_TO_DATE) != null) {
            validBetween.setToDate((ZonedDateTime) input.get(VALID_BETWEEN_TO_DATE));
        }

        return validBetween;
    }
}
