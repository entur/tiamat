package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.PrivateCodeStructure;

import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

public class PrivateCodeMapper {
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
