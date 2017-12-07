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

import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.service.AlternativeNameUpdater;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;
import static org.rutebanken.tiamat.rest.graphql.mappers.PrivateCodeMapper.getPrivateCodeStructure;

@Component
public class GroupOfEntitiesMapper {

    private static final Logger logger = LoggerFactory.getLogger(GroupOfEntitiesMapper.class);

    @Autowired
    private SiteElementMapper siteElementMapper;

    public boolean populate(Map input, GroupOfEntities_VersionStructure entity) {
        boolean isUpdated = false;

        if (input.get(NAME) != null) {
            entity.setName(getEmbeddableString((Map) input.get(NAME)));
            isUpdated = true;
        }
        if (input.get(SHORT_NAME) != null) {
            entity.setShortName(getEmbeddableString((Map) input.get(SHORT_NAME)));
            isUpdated = true;
        }
        if (input.get(DESCRIPTION) != null) {
            entity.setDescription(getEmbeddableString((Map) input.get(DESCRIPTION)));
            isUpdated = true;
        }

        if (input.get(VERSION_COMMENT) != null) {
            entity.setVersionComment((String) input.get(VERSION_COMMENT));
            isUpdated = true;
        }

        if (input.get(KEY_VALUES) != null) {
            List<Map> keyValues = (List) input.get(KEY_VALUES);

            entity.getKeyValues().clear();

            keyValues.forEach(inputMap-> {
                String key = (String)inputMap.get(KEY);
                List<String> values = (List<String>)inputMap.get(VALUES);

                Value value = new Value(values);
                entity.getKeyValues().put(key, value);
            });

            isUpdated = true;
        }

        if(entity instanceof SiteElement) {
            SiteElement siteElement = (SiteElement) entity;
            isUpdated = isUpdated | siteElementMapper.populate(input, siteElement);

        }
        return isUpdated;
    }


}
