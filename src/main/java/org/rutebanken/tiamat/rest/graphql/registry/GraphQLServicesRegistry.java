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

package org.rutebanken.tiamat.rest.graphql.registry;

import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.mappers.GeometryMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.ValidBetweenMapper;
import org.rutebanken.tiamat.rest.graphql.scalars.DateScalar;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.rutebanken.tiamat.service.TagCreator;
import org.rutebanken.tiamat.service.TagRemover;
import org.rutebanken.tiamat.service.TariffZoneTerminator;
import org.rutebanken.tiamat.service.parking.ParkingDeleter;
import org.rutebanken.tiamat.service.stopplace.MultiModalStopPlaceEditor;
import org.rutebanken.tiamat.service.stopplace.StopPlaceDeleter;
import org.rutebanken.tiamat.service.stopplace.StopPlaceMerger;
import org.rutebanken.tiamat.service.stopplace.StopPlaceQuayDeleter;
import org.rutebanken.tiamat.service.stopplace.StopPlaceQuayMerger;
import org.rutebanken.tiamat.service.stopplace.StopPlaceQuayMover;
import org.rutebanken.tiamat.service.stopplace.StopPlaceReopener;
import org.rutebanken.tiamat.service.stopplace.StopPlaceTerminator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Registry that aggregates all GraphQL services and utilities.
 * Provides a single point of access to mappers, editors, deleters, mergers, and other services.
 */
@Component
public class GraphQLServicesRegistry {

    private final ValidBetweenMapper validBetweenMapper;
    private final GeometryMapper geometryMapper;
    private final MultiModalStopPlaceEditor parentStopPlaceEditor;
    private final ParkingDeleter parkingDeleter;
    private final StopPlaceMerger stopPlaceMerger;
    private final StopPlaceQuayMover stopPlaceQuayMover;
    private final StopPlaceQuayMerger stopPlaceQuayMerger;
    private final StopPlaceQuayDeleter stopPlaceQuayDeleter;
    private final StopPlaceDeleter stopPlaceDeleter;
    private final StopPlaceTerminator stopPlaceTerminator;
    private final StopPlaceReopener stopPlaceReopener;
    private final TagRemover tagRemover;
    private final TagCreator tagCreator;
    private final TopographicPlaceRepository topographicPlaceRepository;
    private final TariffZoneTerminator tariffZoneTerminator;
    private final DateScalar dateScalar;
    private final TransportModeScalar transportModeScalar;

    @Autowired
    public GraphQLServicesRegistry(
            ValidBetweenMapper validBetweenMapper,
            GeometryMapper geometryMapper,
            MultiModalStopPlaceEditor parentStopPlaceEditor,
            ParkingDeleter parkingDeleter,
            StopPlaceMerger stopPlaceMerger,
            StopPlaceQuayMover stopPlaceQuayMover,
            StopPlaceQuayMerger stopPlaceQuayMerger,
            StopPlaceQuayDeleter stopPlaceQuayDeleter,
            StopPlaceDeleter stopPlaceDeleter,
            StopPlaceTerminator stopPlaceTerminator,
            StopPlaceReopener stopPlaceReopener,
            TagRemover tagRemover,
            TagCreator tagCreator,
            TopographicPlaceRepository topographicPlaceRepository,
            TariffZoneTerminator tariffZoneTerminator,
            DateScalar dateScalar,
            TransportModeScalar transportModeScalar) {
        this.validBetweenMapper = validBetweenMapper;
        this.geometryMapper = geometryMapper;
        this.parentStopPlaceEditor = parentStopPlaceEditor;
        this.parkingDeleter = parkingDeleter;
        this.stopPlaceMerger = stopPlaceMerger;
        this.stopPlaceQuayMover = stopPlaceQuayMover;
        this.stopPlaceQuayMerger = stopPlaceQuayMerger;
        this.stopPlaceQuayDeleter = stopPlaceQuayDeleter;
        this.stopPlaceDeleter = stopPlaceDeleter;
        this.stopPlaceTerminator = stopPlaceTerminator;
        this.stopPlaceReopener = stopPlaceReopener;
        this.tagRemover = tagRemover;
        this.tagCreator = tagCreator;
        this.topographicPlaceRepository = topographicPlaceRepository;
        this.tariffZoneTerminator = tariffZoneTerminator;
        this.dateScalar = dateScalar;
        this.transportModeScalar = transportModeScalar;
    }

    // Getters for all services

    public ValidBetweenMapper getValidBetweenMapper() {
        return validBetweenMapper;
    }

    public GeometryMapper getGeometryMapper() {
        return geometryMapper;
    }

    public MultiModalStopPlaceEditor getParentStopPlaceEditor() {
        return parentStopPlaceEditor;
    }

    public ParkingDeleter getParkingDeleter() {
        return parkingDeleter;
    }

    public StopPlaceMerger getStopPlaceMerger() {
        return stopPlaceMerger;
    }

    public StopPlaceQuayMover getStopPlaceQuayMover() {
        return stopPlaceQuayMover;
    }

    public StopPlaceQuayMerger getStopPlaceQuayMerger() {
        return stopPlaceQuayMerger;
    }

    public StopPlaceQuayDeleter getStopPlaceQuayDeleter() {
        return stopPlaceQuayDeleter;
    }

    public StopPlaceDeleter getStopPlaceDeleter() {
        return stopPlaceDeleter;
    }

    public StopPlaceTerminator getStopPlaceTerminator() {
        return stopPlaceTerminator;
    }

    public StopPlaceReopener getStopPlaceReopener() {
        return stopPlaceReopener;
    }

    public TagRemover getTagRemover() {
        return tagRemover;
    }

    public TagCreator getTagCreator() {
        return tagCreator;
    }

    public TopographicPlaceRepository getTopographicPlaceRepository() {
        return topographicPlaceRepository;
    }

    public TariffZoneTerminator getTariffZoneTerminator() {
        return tariffZoneTerminator;
    }

    public DateScalar getDateScalar() {
        return dateScalar;
    }

    public TransportModeScalar getTransportModeScalar() {
        return transportModeScalar;
    }
}
