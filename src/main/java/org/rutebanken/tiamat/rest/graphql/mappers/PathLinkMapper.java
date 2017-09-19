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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;

@Component
public class PathLinkMapper {

    private static final Logger logger = LoggerFactory.getLogger(PathLinkMapper.class);


    private final GeometryMapper geometryMapper;
    private final IdMapper idMapper;

    @Autowired
    public PathLinkMapper(GeometryMapper geometryMapper, IdMapper idMapper) {
        this.geometryMapper = geometryMapper;
        this.idMapper = idMapper;
    }

    public PathLink map(Map input) {

        PathLink pathLink = new PathLink();
        idMapper.extractAndSetNetexId(ID, input, pathLink);

        if(input.get(VERSION) != null) {
            pathLink.setVersion((Long) input.get(VERSION));
        }

        if(input.get(PATH_LINK_FROM) != null) {
            pathLink.setFrom(mapToPathLinkEnd(PATH_LINK_FROM, input));
        }

        if(input.get(PATH_LINK_TO) != null) {
            pathLink.setTo(mapToPathLinkEnd(PATH_LINK_TO, input));
        }

        if(input.get(GEOMETRY) != null) {
            pathLink.setLineString(geometryMapper.createGeoJsonLineString((Map) input.get(GEOMETRY)));
        }

        if(input.get(TRANSFER_DURATION) != null) {
            pathLink.setTransferDuration(mapToTransferDuration((Map) input.get(TRANSFER_DURATION)));
        }
        // TODO
        // allowed use


        return pathLink;
    }

    private TransferDuration mapToTransferDuration(Map input) {
        TransferDuration transferDuration = new TransferDuration();
        transferDuration.setFrequentTravellerDuration(ofSeconds(input, FREQUENT_TRAVELLER_DURATION));
        transferDuration.setMobilityRestrictedTravellerDuration(ofSeconds(input, MOBILITY_RESTRICTED_TRAVELLER_DURATION));
        transferDuration.setOccasionalTravellerDuration(ofSeconds(input, OCCASIONAL_TRAVELLER_DURATION));
        transferDuration.setDefaultDuration(ofSeconds(input, DEFAULT_DURATION));
        return transferDuration;
    }

    private Duration ofSeconds(Map input, String field) {
        if(input.get(field) != null) {
            return Duration.ofSeconds((Integer) input.get(field));
        }
        return null;
    }

    private PathLinkEnd mapToPathLinkEnd(String field, Map input) {
        if(input.get(field) != null) {
            PathLinkEnd pathLinkEnd = mapToPathLinkEnd((Map) input.get(field));
            return pathLinkEnd;
        }
        return null;
    }

    private PathLinkEnd mapToPathLinkEnd(Map input) {
        PathLinkEnd pathLinkEnd = new PathLinkEnd();
        idMapper.extractAndSetNetexId(ID, input, pathLinkEnd);

        if(input.get(PATH_LINK_END_PLACE_REF) != null) {

            @SuppressWarnings("unchecked")
            Map<String, String> placeRefInput = (Map<String, String>) input.get(PATH_LINK_END_PLACE_REF);
            String reference = placeRefInput.get(ENTITY_REF_REF);
            String version = placeRefInput.get(ENTITY_REF_VERSION);

            AddressablePlaceRefStructure addressablePlaceRef = new AddressablePlaceRefStructure(reference, version);
            pathLinkEnd.setPlaceRef(addressablePlaceRef);
        } else {
            throw new IllegalArgumentException("Expected PathLinkEnd " + pathLinkEnd + " to contain PlaceRef");
        }

        logger.trace("Mapped {}", pathLinkEnd);
        return pathLinkEnd;
    }
}
