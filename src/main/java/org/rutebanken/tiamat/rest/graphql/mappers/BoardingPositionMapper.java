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

import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.model.BoardingPosition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GEOMETRY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PUBLIC_CODE;

@Component
public class BoardingPositionMapper {

    private static final Logger logger = LoggerFactory.getLogger(BoardingPositionMapper.class);

    @Autowired
    private GeometryMapper geometryMapper;

    @SuppressWarnings("unchecked")
    public BoardingPosition mapBoardingPosition(Map entry) {

        final String publicCode = (String) entry.get(PUBLIC_CODE);
        final Point geoJsonPoint = geometryMapper.createGeoJsonPoint((Map) entry.get(GEOMETRY));

        BoardingPosition boardingPosition = new BoardingPosition();
        boardingPosition.setPublicCode(publicCode);
        boardingPosition.setCentroid(geoJsonPoint);

        return boardingPosition;
    }

    public List<BoardingPosition> mapBoardingPositions(List boardingPositions) {
        List<BoardingPosition> mapped =  new ArrayList<>();
        for(Object object : boardingPositions) {
            if (object instanceof Map) {
                mapped.add(mapBoardingPosition((Map) object));
            } else {
                logger.warn("Object not instance of Map: {}", object);
            }
        }
        return mapped;
    }

}
