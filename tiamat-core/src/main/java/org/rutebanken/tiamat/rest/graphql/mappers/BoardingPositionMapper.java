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

import org.rutebanken.tiamat.model.BoardingPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GEOMETRY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PUBLIC_CODE;

@Component
public class BoardingPositionMapper {

    @Autowired
    private GeometryMapper geometryMapper;

    /**
     * Copies the input fields onto a boarding position. A field that the input leaves out stays unchanged,
     * and a {@code publicCode} or {@code geometry} that the input sets to null clears it.
     */
    public void populateBoardingPosition(Map<String, Object> entry, BoardingPosition boardingPosition) {
        if (entry.containsKey(PUBLIC_CODE)) {
            boardingPosition.setPublicCode((String) entry.get(PUBLIC_CODE));
        }
        if (entry.containsKey(GEOMETRY)) {
            boardingPosition.setCentroid(geometryMapper.createGeoJsonPoint((Map) entry.get(GEOMETRY)));
        }
    }
}
