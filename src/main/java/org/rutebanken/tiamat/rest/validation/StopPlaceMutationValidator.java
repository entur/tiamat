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

package org.rutebanken.tiamat.rest.validation;

import com.google.api.client.util.Preconditions;
import com.google.api.client.util.Strings;
import org.rutebanken.tiamat.model.ModificationEnumeration;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;

@Component
public class StopPlaceMutationValidator {

    private final StopPlaceRepository stopPlaceRepository;

    private static final Map<VehicleModeEnumeration, Set<StopTypeEnumeration>> VALID_STOP_TYPES_FOR_MODE;

    static {
        Map<VehicleModeEnumeration, Set<StopTypeEnumeration>> map = new EnumMap<>(VehicleModeEnumeration.class);
        map.put(VehicleModeEnumeration.AIR,         EnumSet.of(StopTypeEnumeration.AIRPORT,        StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.BUS,         EnumSet.of(StopTypeEnumeration.ONSTREET_BUS,   StopTypeEnumeration.BUS_STATION,    StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.CABLEWAY,    EnumSet.of(StopTypeEnumeration.LIFT_STATION,   StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.COACH,       EnumSet.of(StopTypeEnumeration.COACH_STATION,  StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.FERRY,       EnumSet.of(StopTypeEnumeration.FERRY_PORT,     StopTypeEnumeration.FERRY_STOP,     StopTypeEnumeration.HARBOUR_PORT, StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.FUNICULAR,   EnumSet.of(StopTypeEnumeration.LIFT_STATION,   StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.LIFT,        EnumSet.of(StopTypeEnumeration.LIFT_STATION,   StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.METRO,       EnumSet.of(StopTypeEnumeration.METRO_STATION,  StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.RAIL,        EnumSet.of(StopTypeEnumeration.RAIL_STATION,   StopTypeEnumeration.VEHICLE_RAIL_INTERCHANGE, StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.TRAM,        EnumSet.of(StopTypeEnumeration.ONSTREET_TRAM,  StopTypeEnumeration.TRAM_STATION,   StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.TROLLEY_BUS, EnumSet.of(StopTypeEnumeration.ONSTREET_BUS,   StopTypeEnumeration.BUS_STATION,    StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.WATER,       EnumSet.of(StopTypeEnumeration.HARBOUR_PORT,   StopTypeEnumeration.FERRY_PORT,     StopTypeEnumeration.FERRY_STOP,   StopTypeEnumeration.OTHER));
        map.put(VehicleModeEnumeration.OTHER,       EnumSet.of(StopTypeEnumeration.OTHER));
        VALID_STOP_TYPES_FOR_MODE = Collections.unmodifiableMap(map);
    }

    @Autowired
    public StopPlaceMutationValidator(StopPlaceRepository stopPlaceRepository) {
        this.stopPlaceRepository = stopPlaceRepository;
    }

    public StopPlace validateStopPlaceUpdate(String netexId, boolean isParentMutation) throws IllegalArgumentException {
        StopPlace existingStopPlace = findAndVerifyExists(netexId);
        validateNotDeleted(existingStopPlace);
        validateParentChildType(existingStopPlace, isParentMutation, netexId);
        return existingStopPlace;
    }

    public void validateStopPlaceName(StopPlace stopPlace) throws IllegalArgumentException {
        Preconditions.checkArgument(
                stopPlace.getName() != null && !Strings.isNullOrEmpty(stopPlace.getName().getValue()),
                "Stop place must have name set: %s", stopPlace
        );
    }

    public void validateStopPlaceMutation(StopPlace mutatedStopPlace) throws IllegalArgumentException {
        validateStopPlaceName(mutatedStopPlace);
        validateStopPlaceTypeForTransportMode(mutatedStopPlace);
    }

    public void validateChildBelongsToParent(StopPlace child, StopPlace parent) throws IllegalArgumentException {
        Preconditions.checkArgument(
                child.getParentSiteRef() != null,
                "Child stop [id = %s] does not belong to any parent", child.getNetexId()
        );

        Preconditions.checkArgument(
                child.getParentSiteRef().getRef().equals(parent.getNetexId()),
                "Child stop [id = %s] does not belong to parent %s",
                child.getNetexId(), parent.getNetexId()
        );

        Preconditions.checkArgument(
                child.getParentSiteRef().getVersion().equals(String.valueOf(parent.getVersion())),
                "Child stop [id = %s] does not refer to parent %s in correct version: %s",
                child.getNetexId(), parent.getNetexId(), parent.getVersion()
        );
    }

    public void verifyStopPlaceNotNull(StopPlace stopPlace, String netexId) throws IllegalArgumentException {
        Preconditions.checkArgument(stopPlace != null, "Attempting to update StopPlace [id = %s], but StopPlace does not exist.", netexId);
    }

    public static void validateStopPlaceTypeForTransportMode(StopPlace stopPlace) {
        VehicleModeEnumeration transportMode = stopPlace.getTransportMode();
        StopTypeEnumeration stopPlaceType = stopPlace.getStopPlaceType();

        if (transportMode == null || stopPlaceType == null) {
            return;
        }

        Set<StopTypeEnumeration> validTypes = VALID_STOP_TYPES_FOR_MODE.get(transportMode);
        Preconditions.checkArgument(
                validTypes != null && validTypes.contains(stopPlaceType),
                "StopPlaceType %s is not valid for TransportMode %s. Valid types are: %s",
                stopPlaceType, transportMode, validTypes
        );
    }

    private StopPlace findAndVerifyExists(String netexId) {
        StopPlace stopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        Preconditions.checkArgument(
                stopPlace != null,
                "Stop place [id = %s] does not exist", netexId
        );
        return stopPlace;
    }

    private void validateNotDeleted(StopPlace stopPlace) {
        Preconditions.checkArgument(
                stopPlace.getModificationEnumeration() == null ||
                        !stopPlace.getModificationEnumeration().equals(ModificationEnumeration.DELETE),
                "Cannot update/reactivate terminated stop place: %s", stopPlace
        );
    }

    private void validateParentChildType(StopPlace stopPlace, boolean isParentMutation, String netexId) {
        if (isParentMutation) {
            Preconditions.checkArgument(
                    stopPlace.isParentStopPlace(),
                    "Cannot update Stop place [id = %s] as parent when it is not a parent", netexId
            );
        } else {
            Preconditions.checkArgument(
                    !stopPlace.isParentStopPlace(),
                    "Cannot update parent stop place [id = %s] with this mutation", netexId
            );

            Preconditions.checkArgument(
                    stopPlace.getParentSiteRef() == null,
                    "Cannot update stop place [id = %s] which has parent. Edit parent instead: %s",
                    netexId, stopPlace.getParentSiteRef()
            );
        }
    }
}

