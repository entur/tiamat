package org.rutebanken.tiamat.rest.graphql.resolver;

import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PrivateCodeStructure;

import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

public class ObjectResolver {

    public static EmbeddableMultilingualString getEmbeddableString(Map map) {
        if (map != null) {
            return new EmbeddableMultilingualString((String) map.get(VALUE), (String) map.get(LANG));
        }
        return null;
    }


    public static PrivateCodeStructure getPrivateCodeStructure(Map<String, String> pCode) {
        if (pCode == null) {
            return null;
        }
        PrivateCodeStructure privateCode = new PrivateCodeStructure();
        privateCode.setValue(pCode.get(VALUE));
        privateCode.setType(pCode.get(TYPE));
        return privateCode;
    }
}
