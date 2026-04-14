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

import com.google.api.client.util.Preconditions;
import org.rutebanken.tiamat.model.AirSubmodeEnumeration;
import org.rutebanken.tiamat.model.BusSubmodeEnumeration;
import org.rutebanken.tiamat.model.FunicularSubmodeEnumeration;
import org.rutebanken.tiamat.model.InterchangeWeightingEnumeration;
import org.rutebanken.tiamat.model.MetroSubmodeEnumeration;
import org.rutebanken.tiamat.model.PostalAddress;
import org.rutebanken.tiamat.model.PrivateCodeStructure;
import org.rutebanken.tiamat.model.RailSubmodeEnumeration;
import org.rutebanken.tiamat.model.SiteFacilitySet;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TelecabinSubmodeEnumeration;
import org.rutebanken.tiamat.model.TramSubmodeEnumeration;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.model.WaterSubmodeEnumeration;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ADJACENT_SITES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTITY_REF_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FACILITIES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARENT_SITE_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.POSTAL_ADDRESS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PRIVATE_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PUBLIC_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.QUAYS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_PLACE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SUBMODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TRANSPORT_MODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.URL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALUE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WEIGHTING;

@Component
public class StopPlaceMapper {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceMapper.class);

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
    private QuayMapper quayMapper;

    @Autowired
    private GroupOfEntitiesMapper groupOfEntitiesMapper;

    @Autowired
    private StopPlaceTariffZoneRefsMapper stopPlaceTariffZoneRefsMapper;

    @Autowired
    private ValidBetweenMapper validBetweenMapper;

    @Autowired
    private FacilitiesMapper facilitiesMapper;

    @Autowired
    private PostalAddressMapper postalAddressMapper;

    /**
     * @param input
     * @param stopPlace
     * @return true if StopPlace or any og the attached Quays are updated
     */
    public boolean populateStopPlaceFromInput(Map input, StopPlace stopPlace) {
        boolean isUpdated = false;

        if (input.get(STOP_PLACE_TYPE) != null) {
            stopPlace.setStopPlaceType((StopTypeEnumeration) input.get(STOP_PLACE_TYPE));
            isUpdated = true;
        }

        if (input.get(VALID_BETWEEN) != null) {
            stopPlace.setValidBetween(validBetweenMapper.map((Map) input.get(VALID_BETWEEN)));
            isUpdated = true;
        }

        if (input.get(WEIGHTING) != null) {
            stopPlace.setWeighting((InterchangeWeightingEnumeration) input.get(WEIGHTING));
            isUpdated = true;
        }

        if (input.get(PARENT_SITE_REF) != null) {
            SiteRefStructure parentSiteRef = new SiteRefStructure();
            parentSiteRef.setRef((String) input.get(PARENT_SITE_REF));
            stopPlace.setParentSiteRef(parentSiteRef);
            isUpdated = true;
        }
        if (input.get(ADJACENT_SITES) != null) {
            stopPlace.getAdjacentSites().clear();
            List adjacentSiteObjects = (List) input.get(ADJACENT_SITES);
            for(Object adjacentSiteObject : adjacentSiteObjects) {
                Map adjacentMap = (Map) adjacentSiteObject;
                SiteRefStructure siteRefStructure = new SiteRefStructure((String) adjacentMap.get(ENTITY_REF_REF));
                logger.trace("Adding siteRefStructure {} for stop place {}", siteRefStructure, stopPlace);
                stopPlace.getAdjacentSites().add(siteRefStructure);
            }
            isUpdated = true;
        }

        if (input.get(PUBLIC_CODE) != null) {
            stopPlace.setPublicCode((String) input.get(PUBLIC_CODE));
            isUpdated = true;
        }

        if(input.get(PRIVATE_CODE) != null) {
            Map privateCodeInputMap = (Map) input.get(PRIVATE_CODE);
            if(stopPlace.getPrivateCode() == null) {
                stopPlace.setPrivateCode(new PrivateCodeStructure());
            }
            stopPlace.getPrivateCode().setType((String) privateCodeInputMap.get(TYPE));
            stopPlace.getPrivateCode().setValue((String) privateCodeInputMap.get(VALUE));
            isUpdated = true;
        }

        if (input.get(URL) != null) {
            stopPlace.setUrl((String) input.get(URL));
            isUpdated = true;
        }

        if (input.containsKey(POSTAL_ADDRESS)) {
            isUpdated = postalAddressMapper.populatePostalAddressFromInput(stopPlace, (Map) input.get(POSTAL_ADDRESS));
        }

        isUpdated |= stopPlaceTariffZoneRefsMapper.populate(input, stopPlace);

        isUpdated = isUpdated | setTransportModeSubMode(stopPlace, input.get(TRANSPORT_MODE), input.get(SUBMODE));

        if (input.get(STOP_PLACE_TYPE) != null || input.get(TRANSPORT_MODE) != null) {
            validateStopPlaceTypeForTransportMode(stopPlace);
        }

        if (input.get(QUAYS) != null) {
            List quays = (List) input.get(QUAYS);
            for (Object quayObject : quays) {

                Map quayInputMap = (Map) quayObject;
                if (quayMapper.populateQuayFromInput(stopPlace, quayInputMap)) {
                    isUpdated = true;
                } else {
                    logger.info("Quay not changed");
                }
            }
        }
        isUpdated = isUpdated | groupOfEntitiesMapper.populate(input, stopPlace) | setFacilities(stopPlace, input.get(FACILITIES));

        return isUpdated;
    }

    private boolean setTransportModeSubMode(StopPlace stopPlace, Object transportMode, Object submode) {
        if (transportMode != null) {
            stopPlace.setTransportMode((VehicleModeEnumeration) transportMode);

            //Resetting all submodes
            stopPlace.setBusSubmode(null);
            stopPlace.setTramSubmode(null);
            stopPlace.setRailSubmode(null);
            stopPlace.setMetroSubmode(null);
            stopPlace.setAirSubmode(null);
            stopPlace.setWaterSubmode(null);
            stopPlace.setTelecabinSubmode(null);
            stopPlace.setFunicularSubmode(null);

            if (submode != null) {

                VehicleModeEnumeration stopPlaceTransportMode = stopPlace.getTransportMode();

                Preconditions.checkNotNull(stopPlaceTransportMode);
                List<String> validSubmodes = TransportModeScalar.getValidSubmodes(stopPlaceTransportMode.value());

                String errorMessage = "Submode " + submode + " is invalid for TransportMode " + stopPlaceTransportMode;

                switch (submode) {
                    case BusSubmodeEnumeration busSubmodeEnumeration -> {
                        Preconditions.checkArgument(validSubmodes.contains(busSubmodeEnumeration.value()), errorMessage);
                        stopPlace.setBusSubmode(busSubmodeEnumeration);
                    }
                    case TramSubmodeEnumeration tramSubmodeEnumeration -> {
                        Preconditions.checkArgument(validSubmodes.contains(tramSubmodeEnumeration.value()), errorMessage);
                        stopPlace.setTramSubmode(tramSubmodeEnumeration);
                    }
                    case RailSubmodeEnumeration railSubmodeEnumeration -> {
                        Preconditions.checkArgument(validSubmodes.contains(railSubmodeEnumeration.value()), errorMessage);
                        stopPlace.setRailSubmode(railSubmodeEnumeration);
                    }
                    case MetroSubmodeEnumeration metroSubmodeEnumeration -> {
                        Preconditions.checkArgument(validSubmodes.contains(metroSubmodeEnumeration.value()), errorMessage);
                        stopPlace.setMetroSubmode(metroSubmodeEnumeration);
                    }
                    case AirSubmodeEnumeration airSubmodeEnumeration -> {
                        Preconditions.checkArgument(validSubmodes.contains(airSubmodeEnumeration.value()), errorMessage);
                        stopPlace.setAirSubmode(airSubmodeEnumeration);
                    }
                    case WaterSubmodeEnumeration waterSubmodeEnumeration -> {
                        Preconditions.checkArgument(validSubmodes.contains(waterSubmodeEnumeration.value()), errorMessage);
                        stopPlace.setWaterSubmode(waterSubmodeEnumeration);
                    }
                    case TelecabinSubmodeEnumeration telecabinSubmodeEnumeration -> {
                        Preconditions.checkArgument(validSubmodes.contains(telecabinSubmodeEnumeration.value()), errorMessage);
                        stopPlace.setTelecabinSubmode(telecabinSubmodeEnumeration);
                    }
                    case FunicularSubmodeEnumeration funicularSubmodeEnumeration -> {
                        Preconditions.checkArgument(validSubmodes.contains(funicularSubmodeEnumeration.value()), errorMessage);
                        stopPlace.setFunicularSubmode(funicularSubmodeEnumeration);
                    }
                    default -> {
                    }
                }
            }
            return true;
        }
        return false;
    }

    private void validateStopPlaceTypeForTransportMode(StopPlace stopPlace) {
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

    private boolean setFacilities(StopPlace stopPlace, Object facilitiesListObject) {
        Set<SiteFacilitySet> facilities = facilitiesMapper.mapFacilities((List) facilitiesListObject);
        if (Objects.equals(stopPlace.getFacilities(), facilities)) {
            return false;
        }
        stopPlace.setFacilities(facilities);
        return true;
    }
}
