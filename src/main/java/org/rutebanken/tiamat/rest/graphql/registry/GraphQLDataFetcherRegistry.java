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

import graphql.schema.DataFetcher;
import org.rutebanken.tiamat.model.FareZone;
import org.rutebanken.tiamat.model.GroupOfStopPlaces;
import org.rutebanken.tiamat.model.GroupOfTariffZones;
import org.rutebanken.tiamat.model.PurposeOfGrouping;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.rest.graphql.fetchers.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Registry that aggregates all GraphQL data fetchers.
 * Provides a single point of access to all query and mutation data fetchers.
 */
@Component
public class GraphQLDataFetcherRegistry {

    private final DataFetcher stopPlaceFetcher;
    private final DataFetcher<Page<GroupOfStopPlaces>> groupOfStopPlacesFetcher;
    private final DataFetcher<Page<PurposeOfGrouping>> purposeOfGroupingFetcher;
    private final DataFetcher<Page<GroupOfTariffZones>> groupOfTariffZonesFetcher;
    private final DataFetcher<GroupOfStopPlaces> groupOfStopPlacesUpdater;
    private final DataFetcher<PurposeOfGrouping> purposeOfGroupingUpdater;
    private final DataFetcher<Boolean> groupOfStopPlacesDeleterFetcher;
    private final DataFetcher<Page<TariffZone>> tariffZonesFetcher;
    private final StopPlaceTariffZoneFetcher stopPlaceTariffZoneFetcher;
    private final EntityPermissionsFetcher entityPermissionsFetcher;
    private final LocationPermissionsFetcher locationPermissionsFetcher;
    private final StopPlaceFareZoneFetcher stopPlaceFareZoneFetcher;
    private final DataFetcher<Page<FareZone>> fareZonesFetcher;
    private final DataFetcher pathLinkFetcher;
    private final DataFetcher pathLinkUpdater;
    private final DataFetcher topographicPlaceFetcher;
    private final DataFetcher stopPlaceUpdater;
    private final DataFetcher parkingFetcher;
    private final DataFetcher parkingUpdater;
    private final FareZoneAuthoritiesFetcher fareZoneAuthoritiesFetcher;
    private final DataFetcher<List<GroupOfStopPlaces>> stopPlaceGroupsFetcher;
    private final KeyValuesDataFetcher keyValuesDataFetcher;
    private final PolygonFetcher polygonFetcher;
    private final DataFetcher referenceFetcher;
    private final GroupOfStopPlacesMembersFetcher groupOfStopPlacesMembersFetcher;
    private final GroupOfStopPlacesPurposeOfGroupingFetcher groupOfStopPlacesPurposeOfGroupingFetcher;
    private final TagFetcher tagFetcher;
    private final UserPermissionsFetcher userPermissionsFetcher;

    @Autowired
    public GraphQLDataFetcherRegistry(
            DataFetcher stopPlaceFetcher,
            DataFetcher<Page<GroupOfStopPlaces>> groupOfStopPlacesFetcher,
            DataFetcher<Page<PurposeOfGrouping>> purposeOfGroupingFetcher,
            DataFetcher<Page<GroupOfTariffZones>> groupOfTariffZonesFetcher,
            DataFetcher<GroupOfStopPlaces> groupOfStopPlacesUpdater,
            DataFetcher<PurposeOfGrouping> purposeOfGroupingUpdater,
            DataFetcher<Boolean> groupOfStopPlacesDeleterFetcher,
            DataFetcher<Page<TariffZone>> tariffZonesFetcher,
            StopPlaceTariffZoneFetcher stopPlaceTariffZoneFetcher,
            EntityPermissionsFetcher entityPermissionsFetcher,
            LocationPermissionsFetcher locationPermissionsFetcher,
            StopPlaceFareZoneFetcher stopPlaceFareZoneFetcher,
            DataFetcher<Page<FareZone>> fareZonesFetcher,
            DataFetcher pathLinkFetcher,
            DataFetcher pathLinkUpdater,
            DataFetcher topographicPlaceFetcher,
            DataFetcher stopPlaceUpdater,
            DataFetcher parkingFetcher,
            DataFetcher parkingUpdater,
            FareZoneAuthoritiesFetcher fareZoneAuthoritiesFetcher,
            DataFetcher<List<GroupOfStopPlaces>> stopPlaceGroupsFetcher,
            KeyValuesDataFetcher keyValuesDataFetcher,
            PolygonFetcher polygonFetcher,
            DataFetcher referenceFetcher,
            GroupOfStopPlacesMembersFetcher groupOfStopPlacesMembersFetcher,
            GroupOfStopPlacesPurposeOfGroupingFetcher groupOfStopPlacesPurposeOfGroupingFetcher,
            TagFetcher tagFetcher,
            UserPermissionsFetcher userPermissionsFetcher) {
        this.stopPlaceFetcher = stopPlaceFetcher;
        this.groupOfStopPlacesFetcher = groupOfStopPlacesFetcher;
        this.purposeOfGroupingFetcher = purposeOfGroupingFetcher;
        this.groupOfTariffZonesFetcher = groupOfTariffZonesFetcher;
        this.groupOfStopPlacesUpdater = groupOfStopPlacesUpdater;
        this.purposeOfGroupingUpdater = purposeOfGroupingUpdater;
        this.groupOfStopPlacesDeleterFetcher = groupOfStopPlacesDeleterFetcher;
        this.tariffZonesFetcher = tariffZonesFetcher;
        this.stopPlaceTariffZoneFetcher = stopPlaceTariffZoneFetcher;
        this.entityPermissionsFetcher = entityPermissionsFetcher;
        this.locationPermissionsFetcher = locationPermissionsFetcher;
        this.stopPlaceFareZoneFetcher = stopPlaceFareZoneFetcher;
        this.fareZonesFetcher = fareZonesFetcher;
        this.pathLinkFetcher = pathLinkFetcher;
        this.pathLinkUpdater = pathLinkUpdater;
        this.topographicPlaceFetcher = topographicPlaceFetcher;
        this.stopPlaceUpdater = stopPlaceUpdater;
        this.parkingFetcher = parkingFetcher;
        this.parkingUpdater = parkingUpdater;
        this.fareZoneAuthoritiesFetcher = fareZoneAuthoritiesFetcher;
        this.stopPlaceGroupsFetcher = stopPlaceGroupsFetcher;
        this.keyValuesDataFetcher = keyValuesDataFetcher;
        this.polygonFetcher = polygonFetcher;
        this.referenceFetcher = referenceFetcher;
        this.groupOfStopPlacesMembersFetcher = groupOfStopPlacesMembersFetcher;
        this.groupOfStopPlacesPurposeOfGroupingFetcher = groupOfStopPlacesPurposeOfGroupingFetcher;
        this.tagFetcher = tagFetcher;
        this.userPermissionsFetcher = userPermissionsFetcher;
    }

    // Getters for all data fetchers

    public DataFetcher getStopPlaceFetcher() {
        return stopPlaceFetcher;
    }

    public DataFetcher<Page<GroupOfStopPlaces>> getGroupOfStopPlacesFetcher() {
        return groupOfStopPlacesFetcher;
    }

    public DataFetcher<Page<PurposeOfGrouping>> getPurposeOfGroupingFetcher() {
        return purposeOfGroupingFetcher;
    }

    public DataFetcher<Page<GroupOfTariffZones>> getGroupOfTariffZonesFetcher() {
        return groupOfTariffZonesFetcher;
    }

    public DataFetcher<GroupOfStopPlaces> getGroupOfStopPlacesUpdater() {
        return groupOfStopPlacesUpdater;
    }

    public DataFetcher<PurposeOfGrouping> getPurposeOfGroupingUpdater() {
        return purposeOfGroupingUpdater;
    }

    public DataFetcher<Boolean> getGroupOfStopPlacesDeleterFetcher() {
        return groupOfStopPlacesDeleterFetcher;
    }

    public DataFetcher<Page<TariffZone>> getTariffZonesFetcher() {
        return tariffZonesFetcher;
    }

    public StopPlaceTariffZoneFetcher getStopPlaceTariffZoneFetcher() {
        return stopPlaceTariffZoneFetcher;
    }

    public EntityPermissionsFetcher getEntityPermissionsFetcher() {
        return entityPermissionsFetcher;
    }

    public LocationPermissionsFetcher getLocationPermissionsFetcher() {
        return locationPermissionsFetcher;
    }

    public StopPlaceFareZoneFetcher getStopPlaceFareZoneFetcher() {
        return stopPlaceFareZoneFetcher;
    }

    public DataFetcher<Page<FareZone>> getFareZonesFetcher() {
        return fareZonesFetcher;
    }

    public DataFetcher getPathLinkFetcher() {
        return pathLinkFetcher;
    }

    public DataFetcher getPathLinkUpdater() {
        return pathLinkUpdater;
    }

    public DataFetcher getTopographicPlaceFetcher() {
        return topographicPlaceFetcher;
    }

    public DataFetcher getStopPlaceUpdater() {
        return stopPlaceUpdater;
    }

    public DataFetcher getParkingFetcher() {
        return parkingFetcher;
    }

    public DataFetcher getParkingUpdater() {
        return parkingUpdater;
    }

    public FareZoneAuthoritiesFetcher getFareZoneAuthoritiesFetcher() {
        return fareZoneAuthoritiesFetcher;
    }

    public DataFetcher<List<GroupOfStopPlaces>> getStopPlaceGroupsFetcher() {
        return stopPlaceGroupsFetcher;
    }

    public KeyValuesDataFetcher getKeyValuesDataFetcher() {
        return keyValuesDataFetcher;
    }

    public PolygonFetcher getPolygonFetcher() {
        return polygonFetcher;
    }

    public DataFetcher getReferenceFetcher() {
        return referenceFetcher;
    }

    public GroupOfStopPlacesMembersFetcher getGroupOfStopPlacesMembersFetcher() {
        return groupOfStopPlacesMembersFetcher;
    }

    public GroupOfStopPlacesPurposeOfGroupingFetcher getGroupOfStopPlacesPurposeOfGroupingFetcher() {
        return groupOfStopPlacesPurposeOfGroupingFetcher;
    }

    public TagFetcher getTagFetcher() {
        return tagFetcher;
    }

    public UserPermissionsFetcher getUserPermissionsFetcher() {
        return userPermissionsFetcher;
    }
}
