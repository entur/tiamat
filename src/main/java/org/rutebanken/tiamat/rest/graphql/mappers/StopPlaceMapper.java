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
import org.rutebanken.tiamat.model.PrivateCodeStructure;
import org.rutebanken.tiamat.model.RailSubmodeEnumeration;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.StopPlaceOrganisationRef;
import org.rutebanken.tiamat.model.StopPlaceOrganisationRelationshipEnumeration;
import org.rutebanken.tiamat.model.StopTypeEnumeration;
import org.rutebanken.tiamat.model.TelecabinSubmodeEnumeration;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.model.TramSubmodeEnumeration;
import org.rutebanken.tiamat.model.VehicleModeEnumeration;
import org.rutebanken.tiamat.model.WaterSubmodeEnumeration;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ADJACENT_SITES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTITY_REF_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ORGANISATIONS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ORGANISATION_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARENT_SITE_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PRIVATE_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PUBLIC_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.QUAYS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.RELATIONSHIP_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.STOP_PLACE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SUBMODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TOPOGRAPHIC_PLACE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TRANSPORT_MODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALUE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.WEIGHTING;

@Component
public class StopPlaceMapper {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceMapper.class);

    @Autowired
    private QuayMapper quayMapper;

    @Autowired
    private GroupOfEntitiesMapper groupOfEntitiesMapper;

    @Autowired
    private StopPlaceTariffZoneRefsMapper stopPlaceTariffZoneRefsMapper;

    @Autowired
    private ValidBetweenMapper validBetweenMapper;

    @Autowired
    private TopographicPlaceRepository topographicPlaceRepository;

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
        if (input.get(ORGANISATIONS) != null) {
            stopPlace.getOrganisations().clear();
            List organisations = (List) input.get(ORGANISATIONS);
            for(Object organisationObject : organisations) {
                Map organisationMap = (Map) organisationObject;
                StopPlaceOrganisationRef organisationRef = new StopPlaceOrganisationRef(
                        (String) organisationMap.get(ORGANISATION_REF),
                        (StopPlaceOrganisationRelationshipEnumeration) organisationMap.get(RELATIONSHIP_TYPE)
                );
                logger.trace("Adding organisation {} for stop place {}", organisationRef, stopPlace);
                stopPlace.getOrganisations().add(organisationRef);
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

        if (input.get(TOPOGRAPHIC_PLACE) != null) {
            Map topographicPlaceInputMap = (Map) input.get(TOPOGRAPHIC_PLACE);
            String topographicPlaceRef = (String) topographicPlaceInputMap.get(ID);
            TopographicPlace topographicPlace = topographicPlaceRepository.findFirstByNetexIdOrderByVersionDesc(topographicPlaceRef);
            stopPlace.setTopographicPlace(topographicPlace);
            isUpdated = true;
        }

        isUpdated |= stopPlaceTariffZoneRefsMapper.populate(input, stopPlace);

        isUpdated = isUpdated | setTransportModeSubMode(stopPlace, input.get(TRANSPORT_MODE), input.get(SUBMODE));

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
        isUpdated = isUpdated | groupOfEntitiesMapper.populate(input, stopPlace);

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

                if (submode instanceof BusSubmodeEnumeration) {
                    Preconditions.checkArgument(validSubmodes.contains(((BusSubmodeEnumeration) submode).value()), errorMessage);
                    stopPlace.setBusSubmode((BusSubmodeEnumeration) submode);
                } else if (submode instanceof TramSubmodeEnumeration) {
                    Preconditions.checkArgument(validSubmodes.contains(((TramSubmodeEnumeration) submode).value()), errorMessage);
                    stopPlace.setTramSubmode((TramSubmodeEnumeration) submode);
                } else if (submode instanceof RailSubmodeEnumeration) {
                    Preconditions.checkArgument(validSubmodes.contains(((RailSubmodeEnumeration) submode).value()), errorMessage);
                    stopPlace.setRailSubmode((RailSubmodeEnumeration) submode);
                } else if (submode instanceof MetroSubmodeEnumeration) {
                    Preconditions.checkArgument(validSubmodes.contains(((MetroSubmodeEnumeration) submode).value()), errorMessage);
                    stopPlace.setMetroSubmode((MetroSubmodeEnumeration) submode);
                } else if (submode instanceof AirSubmodeEnumeration) {
                    Preconditions.checkArgument(validSubmodes.contains(((AirSubmodeEnumeration) submode).value()), errorMessage);
                    stopPlace.setAirSubmode((AirSubmodeEnumeration) submode);
                } else if (submode instanceof WaterSubmodeEnumeration) {
                    Preconditions.checkArgument(validSubmodes.contains(((WaterSubmodeEnumeration) submode).value()), errorMessage);
                    stopPlace.setWaterSubmode((WaterSubmodeEnumeration) submode);
                } else if (submode instanceof TelecabinSubmodeEnumeration) {
                    Preconditions.checkArgument(validSubmodes.contains(((TelecabinSubmodeEnumeration) submode).value()),errorMessage);
                    stopPlace.setTelecabinSubmode((TelecabinSubmodeEnumeration) submode);
                } else if (submode instanceof FunicularSubmodeEnumeration) {
                    Preconditions.checkArgument(validSubmodes.contains(((FunicularSubmodeEnumeration) submode).value()),errorMessage);
                    stopPlace.setFunicularSubmode((FunicularSubmodeEnumeration) submode);
                }
            }
            return true;
        }
        return false;
    }


}
