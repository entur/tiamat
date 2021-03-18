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

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GEOMETRY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PRIVATE_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SHORT_NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN;

import java.util.Map;

import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TariffZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * This class maps input fields onto a TariffZone object.
 *
 * @author Dick Zetterberg (dick@transitor.se)
 * @version 2020-06-01
 */
@Component
public class TariffZoneMapper {
    private static final Logger logger = LoggerFactory.getLogger(TariffZoneMapper.class);

    @Autowired
    private ValidBetweenMapper validBetweenMapper;

    @Autowired
    private GeometryMapper geometryMapper;

    @Autowired
    public TariffZoneMapper() {
    }

    public boolean populate(Map input, TariffZone tariffZone) {
        boolean isUpdated = false;

        if (input.get(NAME) != null) {
            EmbeddableMultilingualString name = EmbeddableMultilingualStringMapper.getEmbeddableString((Map) input.get(NAME));
            isUpdated |= !name.equals(tariffZone.getName());
            tariffZone.setName(name);
        }

        if (input.get(SHORT_NAME) != null) {
            EmbeddableMultilingualString shortName = EmbeddableMultilingualStringMapper.getEmbeddableString((Map) input.get(SHORT_NAME));
            isUpdated |= !shortName.equals(tariffZone.getShortName());
            tariffZone.setShortName(shortName);
        }

        if (input.get(PRIVATE_CODE) != null) {
            Map<String, String> privateCodeInputMap = (Map) input.get(PRIVATE_CODE);
            tariffZone.setPrivateCode(PrivateCodeMapper.getPrivateCodeStructure(privateCodeInputMap));
            isUpdated = true;
        }

        if (input.get(VALID_BETWEEN) != null) {
            tariffZone.setValidBetween(validBetweenMapper.map((Map) input.get(VALID_BETWEEN)));
            isUpdated = true;
        }

        if (input.get(GEOMETRY) != null) {
            tariffZone.setCentroid(geometryMapper.createGeoJsonPoint((Map) input.get(GEOMETRY)));
            isUpdated = true;
        }

        isUpdated |= KeyValuesMapper.populate(input, tariffZone);

        return isUpdated;
    }
}
