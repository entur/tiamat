/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.graphql.mappers;

import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DESCRIPTION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VERSION_COMMENT;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;

@Component
public class PurposeOfGroupingMapper {


    public boolean populate(Map input, PurposeOfGrouping purposeOfGrouping) {
        boolean isUpdated = false;

        if (input.get(NAME) != null) {
            purposeOfGrouping.setName(getEmbeddableString((Map) input.get(NAME)));
            isUpdated = true;
        }

        if (input.get(DESCRIPTION) != null) {
            purposeOfGrouping.setDescription(getEmbeddableString(((Map) input.get(DESCRIPTION))));
            isUpdated = true;
        } else if (input.get(DESCRIPTION) == null){
            purposeOfGrouping.setDescription(null);
            isUpdated = true;
        }

        if (input.get(VERSION_COMMENT) != null) {
            purposeOfGrouping.setVersionComment((String) input.get(VERSION_COMMENT));
            isUpdated = true;
        }

        return isUpdated;
    }

}
