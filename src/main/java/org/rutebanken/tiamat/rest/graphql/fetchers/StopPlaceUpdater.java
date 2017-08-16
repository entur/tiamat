package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.api.client.repackaged.com.google.common.base.Strings;
import com.google.api.client.util.Preconditions;
import graphql.language.Field;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.rutebanken.helper.organisation.ReflectionAuthorizationService;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.rest.graphql.helpers.CleanupHelper;
import org.rutebanken.tiamat.rest.graphql.mappers.*;
import org.rutebanken.tiamat.rest.graphql.scalars.TransportModeScalar;
import org.rutebanken.tiamat.service.TopographicPlaceLookupService;
import org.rutebanken.tiamat.versioning.StopPlaceVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Instant;
import java.util.*;

import static org.rutebanken.helper.organisation.AuthorizationConstants.ROLE_EDIT_STOPS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.*;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;
import static org.rutebanken.tiamat.rest.graphql.mappers.PrivateCodeMapper.getPrivateCodeStructure;

@Service("stopPlaceUpdater")
@Transactional
class StopPlaceUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(StopPlaceUpdater.class);

    @Autowired
    private StopPlaceVersionedSaverService stopPlaceVersionedSaverService;

    @Autowired
    private StopPlaceRepository stopPlaceRepository;

    @Autowired
    private ReflectionAuthorizationService authorizationService;

    @Autowired
    private ValidBetweenMapper validBetweenMapper;

    @Autowired
    private SiteElementMapper siteElementMapper;

    @Override
    public Object get(DataFetchingEnvironment environment) {
        List<Field> fields = environment.getFields();
        CleanupHelper.trimValues(environment.getArguments());
        StopPlace stopPlace = null;
        for (Field field : fields) {
            if (field.getName().equals(MUTATE_STOPPLACE)) {
                stopPlace = createOrUpdateStopPlace(environment, false);
            } else if(field.getName().equals(MUTATE_PARENT_STOPPLACE)) {
                stopPlace = createOrUpdateStopPlace(environment, true);
            }
        }
        return Arrays.asList(stopPlace);
    }


    private StopPlace createOrUpdateStopPlace(DataFetchingEnvironment environment, boolean mutateParent) {
        StopPlace updatedStopPlace;
        StopPlace existingVersion = null;

        Map input = environment.getArgument(OUTPUT_TYPE_STOPPLACE);
        if(input == null) {
            input = environment.getArgument(OUTPUT_TYPE_PARENT_STOPPLACE);
        }

        if (input != null) {

            String netexId = (String) input.get(ID);
            if (netexId != null) {
                logger.info("About to update StopPlace {}", netexId);

                existingVersion = findAndVerify(netexId);

                if(mutateParent) {
                    Preconditions.checkArgument(existingVersion.isParentStopPlace(),
                            "Attempting to update StopPlace as parent [id = %s], but StopPlace is not a parent", netexId);
                } else {
                    Preconditions.checkArgument(!existingVersion.isParentStopPlace(),
                            "Attempting to update parent StopPlace [id = %s] with incorrect mutation. Use %s", netexId, MUTATE_PARENT_STOPPLACE);
                }

                updatedStopPlace = stopPlaceVersionedSaverService.createCopy(existingVersion, StopPlace.class);

            } else {
                Preconditions.checkArgument(!mutateParent,
                        "Cannot create new parent stop place. Use mutation %s", CREATE_MULTIMODAL_STOPPLACE);

                logger.info("Creating new StopPlace");
                updatedStopPlace = new StopPlace();
            }

            if (updatedStopPlace != null) {
                boolean hasValuesChanged = populateStopPlaceFromInput(input, updatedStopPlace);

                if (hasValuesChanged) {
                    authorizationService.assertAuthorized(ROLE_EDIT_STOPS, Arrays.asList(existingVersion, updatedStopPlace));

                    if(updatedStopPlace.isParentStopPlace()) {
                        handleChildStops(input, updatedStopPlace);
                    }

                    if(updatedStopPlace.getName() == null || Strings.isNullOrEmpty(updatedStopPlace.getName().getValue())) {
                        throw new IllegalArgumentException("Updated stop place must have name set: " + updatedStopPlace);
                    }

                    updatedStopPlace = stopPlaceVersionedSaverService.saveNewVersion(existingVersion, updatedStopPlace);

                    return updatedStopPlace;
                }
            }
        }
        return existingVersion;
    }

    private void handleChildStops(Map input, StopPlace updatedParentStopPlace) {
        if(input.get(CHILDREN) != null) {
            List childObjects = (List) input.get(CHILDREN);
            logger.info("Incoming child stop objects: {}", childObjects);

            List<StopPlace> populatedChilds =  new ArrayList<>();

            for(Object childStopObject : childObjects) {
                Map childStopMap = (Map) childStopObject;
                String childNetexId = (String) childStopMap.get(ID);

                if(updatedParentStopPlace.getChildren().stream().noneMatch(child -> child.getNetexId().equals(childNetexId))) {
                    throw new RuntimeException("Parent " + updatedParentStopPlace.getNetexId() + " does not already contain this child " + childNetexId + ". Cannot continue.");
                }

                StopPlace existingChildStopPlace = findAndVerify(childNetexId);

                verifyCorrectParentSet(existingChildStopPlace, updatedParentStopPlace);

                StopPlace child = new StopPlace();
                populateStopPlaceFromInput((Map) childStopMap, child);

                populatedChilds.add(child);
            }


            logger.info("Populated child stops: {}", populatedChilds);
        }
    }

    private StopPlace findAndVerify(String netexId) {
        StopPlace existingStopPlace = stopPlaceRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
        Preconditions.checkArgument(existingStopPlace != null, "Attempting to update StopPlace [id = %s], but StopPlace does not exist.", netexId);
        return existingStopPlace;
    }

    private void verifyCorrectParentSet(StopPlace existingChildStop, StopPlace existingParentStop) {
        Preconditions.checkArgument(existingChildStop.getParentSiteRef() != null,
                "Attempting to update StopPlace child [id = %s], but it does not belong to any parent.", existingChildStop.getNetexId());

        Preconditions.checkArgument(existingChildStop.getParentSiteRef().getRef().equals(existingParentStop.getNetexId()),
                "Attempting to update StopPlace child [id = %s], but it does not belong to parent %s.", existingChildStop.getNetexId(), existingParentStop.getNetexId());

        Preconditions.checkArgument(existingChildStop.getParentSiteRef().getVersion().equals(String.valueOf(existingParentStop.getVersion())),
                "Attempting to update StopPlace child [id = %s], but it does not refer to parent %s in correct version: %s.",
                    existingChildStop.getNetexId(),
                    existingParentStop.getNetexId(),
                    existingParentStop.getVersion());

    }

    /**
     * @param input
     * @param stopPlace
     * @return true if StopPlace or any og the attached Quays are updated
     */
    private boolean populateStopPlaceFromInput(Map input, StopPlace stopPlace) {
        boolean isUpdated = siteElementMapper.populate(input, stopPlace);

        if (input.get(STOP_PLACE_TYPE) != null) {
            stopPlace.setStopPlaceType((StopTypeEnumeration) input.get(STOP_PLACE_TYPE));
            isUpdated = true;
        }

        if (input.get(VERSION_COMMENT) != null) {
            stopPlace.setVersionComment((String) input.get(VERSION_COMMENT));
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

        isUpdated = isUpdated | setTransportModeSubMode(stopPlace, input.get(TRANSPORT_MODE), input.get(SUBMODE));

        if (input.get(QUAYS) != null) {
            List quays = (List) input.get(QUAYS);
            for (Object quayObject : quays) {

                Map quayInputMap = (Map) quayObject;
                if (populateQuayFromInput(stopPlace, quayInputMap)) {
                    isUpdated = true;
                } else {
                    logger.info("Quay not changed");
                }
            }
        }

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

    private boolean populateQuayFromInput(StopPlace stopPlace, Map quayInputMap) {
        Quay quay;
        if (quayInputMap.get(ID) != null) {
            Optional<Quay> existingQuay = stopPlace.getQuays().stream()
                    .filter(q -> q.getNetexId() != null)
                    .filter(q -> q.getNetexId().equals(quayInputMap.get(ID))).findFirst();

            Preconditions.checkArgument(existingQuay.isPresent(),
                    "Attempting to update Quay [id = %s] on StopPlace [id = %s] , but Quay does not exist on StopPlace",
                    quayInputMap.get(ID),
                    stopPlace.getNetexId());

            quay = existingQuay.get();
            logger.info("Updating Quay {} for StopPlace {}", quay.getNetexId(), stopPlace.getNetexId());
        } else {
            quay = new Quay();
            logger.info("Creating new Quay");
        }
        boolean isQuayUpdated = siteElementMapper.populate(quayInputMap, quay);

        if (quayInputMap.get(COMPASS_BEARING) != null) {
            quay.setCompassBearing(((BigDecimal) quayInputMap.get(COMPASS_BEARING)).floatValue());
            isQuayUpdated = true;
        }
        if (quayInputMap.get(PUBLIC_CODE) != null) {
            quay.setPublicCode((String) quayInputMap.get(PUBLIC_CODE));
            isQuayUpdated = true;
        }

        if(quayInputMap.get(PRIVATE_CODE) != null) {
            Map privateCodeInputMap = (Map) quayInputMap.get(PRIVATE_CODE);
            if(quay.getPrivateCode() == null) {
                quay.setPrivateCode(new PrivateCodeStructure());
            }
            quay.getPrivateCode().setType((String) privateCodeInputMap.get(TYPE));
            quay.getPrivateCode().setValue((String) privateCodeInputMap.get(VALUE));
            isQuayUpdated = true;
        }

        if (isQuayUpdated) {
            quay.setChanged(Instant.now());

            if (quay.getNetexId() == null) {
                stopPlace.getQuays().add(quay);
            }
        }
        return isQuayUpdated;
    }



}
