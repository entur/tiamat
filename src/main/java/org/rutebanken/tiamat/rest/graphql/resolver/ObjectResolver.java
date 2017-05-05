package org.rutebanken.tiamat.rest.graphql.resolver;

import org.rutebanken.tiamat.model.EmbeddableMultilingualString;

import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LANG;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALUE;

public class ObjectResolver {

    public static EmbeddableMultilingualString getEmbeddableString(Map map) {
        return new EmbeddableMultilingualString((String) map.get(VALUE), (String) map.get(LANG));
    }
}
