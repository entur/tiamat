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

    public Optional<String> extractIdIfPresent(String field, Map input) {
        if(input.get(field) != null) {
            String netexId = (String) input.get(field);
            logger.debug("Detected ID {}", netexId);
            if(netexId.isEmpty()) {
                logger.debug("The ID provided is empty '{}'", netexId);
                return Optional.empty();
            }
            return Optional.of(netexId);

        }
        return Optional.empty();
    }

    public void extractAndSetNetexId(String field, Map input, IdentifiedEntity identifiedEntity) {

        extractIdIfPresent(field, input)
                .ifPresent(netexId -> identifiedEntity.setNetexId(netexId));
    }
}
