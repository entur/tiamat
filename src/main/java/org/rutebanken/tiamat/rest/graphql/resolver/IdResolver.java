package org.rutebanken.tiamat.rest.graphql.resolver;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import org.rutebanken.tiamat.model.indentification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
public class IdResolver {

    private static final Logger logger = LoggerFactory.getLogger(IdResolver.class);

    public Optional<Long> extractIdIfPresent(String field, Map input) {
        if(input.get(field) != null) {
            String nsrId = (String) input.get(field);
            logger.debug("Detected ID {}", nsrId);
            if(nsrId.isEmpty()) {
                logger.debug("The ID provided is empty '{}'", nsrId);
                return null;
            }
            return NetexIdMapper.getOptionalTiamatId(nsrId);

        }
        return Optional.empty();
    }

    public void extractAndSetId(String field, Map input, IdentifiedEntity identifiedEntity) {

        extractIdIfPresent(field, input)
                .ifPresent(id -> identifiedEntity.setId(id));
    }
}
