package org.rutebanken.tiamat.rest.graphql.resolver;

import org.rutebanken.tiamat.model.indentification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class IdResolver {

    private static final Logger logger = LoggerFactory.getLogger(IdResolver.class);

    public Long extractIdIfPresent(String field, Map input) {
        if(input.get(field) != null) {
            String nsrId = (String) input.get(field);
            logger.info("Detected ID {}", nsrId);
            long tiamatId = NetexIdMapper.getTiamatId(nsrId);
            return tiamatId;
        }
        return null;
    }

    public void extractAndSetId(String field, Map input, IdentifiedEntity identifiedEntity) {
        identifiedEntity.setId(extractIdIfPresent(field, input));
    }
}
