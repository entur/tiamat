package org.rutebanken.tiamat.rest.graphql.mappers;


import org.rutebanken.tiamat.model.EmbeddableMultilingualString;

import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LANG;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALUE;

public class EmbeddableMultilingualStringMapper {

    public static EmbeddableMultilingualString getEmbeddableString(Map map) {
        if (map != null) {
            return new EmbeddableMultilingualString((String) map.get(VALUE), (String) map.get(LANG));
        }
        return null;
    }

}
