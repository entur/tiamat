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

package org.rutebanken.tiamat.netex.mapping.converter;

import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.BidirectionalConverter;
import ma.glasnost.orika.metadata.Type;
import org.rutebanken.netex.model.BoardingPositions_RelStructure;
import org.rutebanken.netex.model.LocationStructure;
import org.rutebanken.netex.model.SimplePoint_VersionStructure;
import org.rutebanken.tiamat.model.BoardingPosition;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class BoardingPositionsConverter extends BidirectionalConverter<Set<BoardingPosition>, org.rutebanken.netex.model.BoardingPositions_RelStructure> {
    @Override
    public BoardingPositions_RelStructure convertTo(Set<BoardingPosition> boardingPositions, Type<BoardingPositions_RelStructure> type, MappingContext mappingContext) {
        if (boardingPositions != null && !boardingPositions.isEmpty()) {
            List<org.rutebanken.netex.model.BoardingPosition> netexBoardingPositions = new ArrayList<>();
            for (BoardingPosition boardingPosition : boardingPositions) {
                if (boardingPosition != null
                        && boardingPosition.getPublicCode() != null
                        && !boardingPosition.getPublicCode().isEmpty()) {
                    // Only Include non-empty boarding-positions
                    final org.rutebanken.netex.model.BoardingPosition netexBoardingPosition = new org.rutebanken.netex.model.BoardingPosition();
                    mapperFacade.map(boardingPosition,netexBoardingPosition);
                    netexBoardingPosition.setId(boardingPosition.getNetexId());
                    netexBoardingPosition.setPublicCode(boardingPosition.getPublicCode());

                    if (boardingPosition.getCentroid()!= null) {
                        SimplePoint_VersionStructure simplePoint = new SimplePoint_VersionStructure()
                                .withLocation(new LocationStructure()
                                        .withLatitude(BigDecimal.valueOf(boardingPosition.getCentroid().getY()))
                                        .withLongitude(BigDecimal.valueOf(boardingPosition.getCentroid().getX())));
                        netexBoardingPosition.setCentroid(simplePoint);
                    }


                    netexBoardingPositions.add(netexBoardingPosition);

                }
            }
            if (!netexBoardingPositions.isEmpty()) {
                final BoardingPositions_RelStructure boardingPositionsRelStructure = new BoardingPositions_RelStructure();
                boardingPositionsRelStructure.getBoardingPositionRefOrBoardingPosition().addAll(netexBoardingPositions);
                return boardingPositionsRelStructure;
            }

        }
        return null;
    }

    @Override
    public Set<BoardingPosition> convertFrom(BoardingPositions_RelStructure boardingPositions_relStructure, Type<Set<BoardingPosition>> type, MappingContext mappingContext) {
        return null;
    }
}
