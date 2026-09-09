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

package org.rutebanken.tiamat.rest.graphql.fetchers;

import com.google.common.base.Preconditions;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import org.locationtech.jts.geom.Point;
import org.rutebanken.tiamat.auth.AuthorizationService;
import org.rutebanken.tiamat.model.AccessModeEnumeration;
import org.rutebanken.tiamat.model.AccessibilityAssessment;
import org.rutebanken.tiamat.model.AccessibilityLimitation;
import org.rutebanken.tiamat.model.AlternativeName;
import org.rutebanken.tiamat.model.AvailabilityCondition;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.EntranceEnumeration;
import org.rutebanken.tiamat.model.InfoLink;
import org.rutebanken.tiamat.model.LightingEnumeration;
import org.rutebanken.tiamat.model.LimitationStatusEnumeration;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.ParkingArea;
import org.rutebanken.tiamat.model.ParkingCapacity;
import org.rutebanken.tiamat.model.ParkingEntranceForVehicles;
import org.rutebanken.tiamat.model.ParkingLayoutEnumeration;
import org.rutebanken.tiamat.model.ParkingPaymentProcessEnumeration;
import org.rutebanken.tiamat.model.ParkingProperties;
import org.rutebanken.tiamat.model.ParkingReservationEnumeration;
import org.rutebanken.tiamat.model.ParkingStayEnumeration;
import org.rutebanken.tiamat.model.ParkingTypeEnumeration;
import org.rutebanken.tiamat.model.ParkingUserEnumeration;
import org.rutebanken.tiamat.model.ParkingVehicleEnumeration;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.model.PlaceEquipment;
import org.rutebanken.tiamat.model.SiteRefStructure;
import org.rutebanken.tiamat.model.TypeOfInfolinkEnumeration;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.rest.graphql.mappers.AccessibilityLimitationMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.AlternativeNameMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.GeometryMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.PlaceEquipmentMapper;
import org.rutebanken.tiamat.rest.graphql.mappers.ValidBetweenMapper;
import org.rutebanken.tiamat.service.AlternativeNameUpdater;
import org.rutebanken.tiamat.versioning.VersionCreator;
import org.rutebanken.tiamat.versioning.save.ParkingVersionedSaverService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ACCESSIBILITY_ASSESSMENT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ACCESS_MODES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ALTERNATIVE_NAMES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.AVAILABILITY_CONDITIONS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.BOOKING_URL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DAY_OFFSET;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.DAY_TYPE_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.END_TIME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTRANCE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTRANCE_WIDTH;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ENTRANCE_HEIGHT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PUBLIC_CODE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.FREE_PARKING_OUT_OF_HOURS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.GEOMETRY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.ID;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.INFO_LINKS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.IS_AVAILABLE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.IS_ENTRY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.IS_EXIT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LABEL;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.LIGHTING;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NAME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NUMBER_OF_SPACES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.NUMBER_OF_SPACES_WITH_RECHARGE_POINT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OUTPUT_TYPE_PARKING;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.OVERNIGHT_PARKING_PERMITTED;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARENT_SITE_REF;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_AREAS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_LAYOUT;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_PAYMENT_PROCESS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_PROPERTIES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_RESERVATION;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_STAY_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_USER_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_VEHICLE_TYPE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PARKING_VEHICLE_TYPES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PAYMENT_METHODS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PLACE_EQUIPMENTS;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.PRINCIPAL_CAPACITY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.REAL_TIME_OCCUPANCY_AVAILABLE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.RECHARGING_AVAILABLE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SECURE;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.SPACES;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.START_TIME;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TOTAL_CAPACITY;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.TYPE_OF_INFO_LINK;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.URI;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VALID_BETWEEN;
import static org.rutebanken.tiamat.rest.graphql.GraphQLNames.VEHICLE_ENTRANCES;
import static org.rutebanken.tiamat.rest.graphql.mappers.EmbeddableMultilingualStringMapper.getEmbeddableString;

@Service("parkingUpdater")
@Transactional
class ParkingUpdater implements DataFetcher {

    private static final Logger logger = LoggerFactory.getLogger(ParkingUpdater.class);

    @Autowired
    private ParkingRepository parkingRepository;

    @Autowired
    private ParkingVersionedSaverService parkingVersionedSaverService;

    @Autowired
    private GeometryMapper geometryMapper;

    @Autowired
    private AuthorizationService authorizationService;

    @Autowired
    private ValidBetweenMapper validBetweenMapper;

    @Autowired
    private VersionCreator versionCreator;

    @Autowired
    private AccessibilityLimitationMapper accessibilityLimitationMapper;

    @Autowired
    private PlaceEquipmentMapper placeEquipmentMapper;

    @Autowired
    private AlternativeNameMapper alternativeNameMapper;

    @Autowired
    private AlternativeNameUpdater alternativeNameUpdater;

    @Override
    public Object get(DataFetchingEnvironment environment) {

        List<Map> input = environment.getArgument(OUTPUT_TYPE_PARKING);
        List<Parking> parkings = null;
        if (input != null) {
            parkings = input.stream()
             .map(m -> createOrUpdateParking(m))
            .collect(Collectors.toList());
        }
        return parkings;
    }

    private Parking createOrUpdateParking(Map input) {
        Parking updatedParking;
        Parking existingVersion = null;
        String netexId = (String) input.get(ID);
        if (netexId != null) {
            logger.info("Updating Parking {}", netexId);
            existingVersion = parkingRepository.findFirstByNetexIdOrderByVersionDesc(netexId);
            Preconditions.checkArgument(existingVersion != null, "Attempting to update Parking [id = %s], but Parking does not exist.", netexId);
            updatedParking = versionCreator.createCopy(existingVersion, Parking.class);

        } else {
            logger.info("Creating new Parking");
            updatedParking = new Parking();
        }
        boolean isUpdated = populateParking(input, updatedParking);

        if (isUpdated) {
            authorizationService.verifyCanEditEntities( Arrays.asList(existingVersion, updatedParking));

            logger.info("Saving new version of parking {}", updatedParking);
            updatedParking = parkingVersionedSaverService.saveNewVersion(updatedParking);

            return updatedParking;
        } else {
            logger.info("No changes - Parking {} NOT updated", netexId);
        }
        return existingVersion;
    }

    private boolean populateParking(Map input, Parking updatedParking) {
        boolean isUpdated = false;
        if (input.get(NAME) != null) {
            EmbeddableMultilingualString name = getEmbeddableString((Map) input.get(NAME));
            isUpdated = isUpdated || (!name.equals(updatedParking.getName()));
            updatedParking.setName(name);
        }

        if (input.get(VALID_BETWEEN) != null) {
            updatedParking.setValidBetween(validBetweenMapper.map((Map) input.get(VALID_BETWEEN)));
            isUpdated = true;
        }

        if (input.get(GEOMETRY) != null) {
            Point geoJsonPoint = geometryMapper.createGeoJsonPoint((Map) input.get(GEOMETRY));
            isUpdated = isUpdated || (!geoJsonPoint.equals(updatedParking.getCentroid()));
            updatedParking.setCentroid(geoJsonPoint);
        }

        if (input.get(PARENT_SITE_REF) != null) {
            SiteRefStructure parentSiteRef = new SiteRefStructure();
            parentSiteRef.setRef((String) input.get(PARENT_SITE_REF));

            isUpdated = isUpdated || (!parentSiteRef.equals(updatedParking.getParentSiteRef()));

            updatedParking.setParentSiteRef(parentSiteRef);
        }
        if (input.get(PRINCIPAL_CAPACITY) != null) {
            BigInteger principalCapacity = (BigInteger) input.get(PRINCIPAL_CAPACITY);
            isUpdated = isUpdated || (!principalCapacity.equals(updatedParking.getPrincipalCapacity()));

            updatedParking.setPrincipalCapacity(principalCapacity);
        }

        if (input.get(PARKING_TYPE) != null) {
            ParkingTypeEnumeration parkingType = (ParkingTypeEnumeration) input.get(PARKING_TYPE);
            isUpdated = isUpdated || (!parkingType.equals(updatedParking.getParkingType()));
            updatedParking.setParkingType(parkingType);
        }
        if (input.get(PARKING_VEHICLE_TYPES) != null) {
            List<ParkingVehicleEnumeration> vehicleTypes = (List<ParkingVehicleEnumeration>) input.get(PARKING_VEHICLE_TYPES);
            isUpdated = isUpdated || !(updatedParking.getParkingVehicleTypes().containsAll(vehicleTypes) &&
                    vehicleTypes.containsAll(updatedParking.getParkingVehicleTypes()));

            updatedParking.getParkingVehicleTypes().clear();
            updatedParking.getParkingVehicleTypes().addAll(vehicleTypes);
        }

        if (input.get(PARKING_LAYOUT) != null) {
            ParkingLayoutEnumeration parkingLayout = (ParkingLayoutEnumeration) input.get(PARKING_LAYOUT);
            isUpdated = isUpdated || (!parkingLayout.equals(updatedParking.getParkingLayout()));
            updatedParking.setParkingLayout(parkingLayout);
        }

        if (input.get(OVERNIGHT_PARKING_PERMITTED) != null) {
            Boolean overnightParkingPermitted = (Boolean) input.get(OVERNIGHT_PARKING_PERMITTED);
            isUpdated = isUpdated || (!overnightParkingPermitted.equals(updatedParking.isOvernightParkingPermitted()));
            updatedParking.setOvernightParkingPermitted(overnightParkingPermitted);
        }

        if (input.get(RECHARGING_AVAILABLE) != null) {
            Boolean rechargingAvailable = (Boolean) input.get(RECHARGING_AVAILABLE);
            isUpdated = isUpdated || (!rechargingAvailable.equals(updatedParking.isRechargingAvailable()));
            updatedParking.setRechargingAvailable(rechargingAvailable);
        }

        if (input.get(SECURE) != null) {
            Boolean isSecure = (Boolean) input.get(SECURE);
            isUpdated = isUpdated || (!isSecure.equals(updatedParking.isSecure()));
            updatedParking.setSecure(isSecure);
        }

        if (input.get(REAL_TIME_OCCUPANCY_AVAILABLE) != null) {
            Boolean isRealtimeOccupancyAvailable = (Boolean) input.get(REAL_TIME_OCCUPANCY_AVAILABLE);
            isUpdated = isUpdated || (!isRealtimeOccupancyAvailable.equals(updatedParking.isRealTimeOccupancyAvailable()));
            updatedParking.setRealTimeOccupancyAvailable(isRealtimeOccupancyAvailable);
        }

        if (input.get(FREE_PARKING_OUT_OF_HOURS) != null) {
            Boolean freeParkingOutOfHours = (Boolean) input.get(FREE_PARKING_OUT_OF_HOURS);
            isUpdated = isUpdated || (!freeParkingOutOfHours.equals(updatedParking.isFreeParkingOutOfHours()));
            updatedParking.setFreeParkingOutOfHours(freeParkingOutOfHours);
        }

        if (input.get(PARKING_PAYMENT_PROCESS) != null) {

            List<ParkingPaymentProcessEnumeration> parkingPaymentProcessTypes = (List<ParkingPaymentProcessEnumeration>) input.get(PARKING_PAYMENT_PROCESS);
            isUpdated = isUpdated || !(updatedParking.getParkingPaymentProcess().containsAll(parkingPaymentProcessTypes) &&
                    parkingPaymentProcessTypes.containsAll(updatedParking.getParkingPaymentProcess()));

            updatedParking.getParkingPaymentProcess().clear();
            updatedParking.getParkingPaymentProcess().addAll(parkingPaymentProcessTypes);
        }

        if (input.get(PAYMENT_METHODS) != null) {

            List<PaymentMethodEnumeration> paymentMethods = (List<PaymentMethodEnumeration>) input.get(PAYMENT_METHODS);
            isUpdated = isUpdated || !(updatedParking.getPaymentMethods().containsAll(paymentMethods) &&
                    paymentMethods.containsAll(updatedParking.getPaymentMethods()));

            updatedParking.getPaymentMethods().clear();
            updatedParking.getPaymentMethods().addAll(paymentMethods);
        }

        if (input.get(LIGHTING) != null) {
            LightingEnumeration lighting = (LightingEnumeration) input.get(LIGHTING);
            isUpdated = isUpdated || (!lighting.equals(updatedParking.getLighting()));
            updatedParking.setLighting(lighting);
        }

        if (input.get(PARKING_RESERVATION) != null) {
            ParkingReservationEnumeration parkingReservation = (ParkingReservationEnumeration) input.get(PARKING_RESERVATION);
            isUpdated = isUpdated || (!parkingReservation.equals(updatedParking.getParkingReservation()));
            updatedParking.setParkingReservation(parkingReservation);
        }

        if (input.get(BOOKING_URL) != null) {
            String bookingUrl = (String) input.get(BOOKING_URL);
            isUpdated = isUpdated || (!bookingUrl.equals(updatedParking.getBookingUrl()));
            updatedParking.setBookingUrl(bookingUrl);
        }

        if (input.get(PARKING_PROPERTIES) != null) {
            List<ParkingProperties> parkingPropertiesList = resolveParkingPropertiesList((List) input.get(PARKING_PROPERTIES));
            int total_capacity = parkingPropertiesList.stream()
                    .map(ParkingProperties::getSpaces)
                    .filter(Objects::nonNull)
                    .flatMap(Collection::stream)
                    .filter(space -> space.getNumberOfSpaces() != null)
                    .mapToInt(space -> space.getNumberOfSpaces().intValue())
                    .sum();
            isUpdated = true;
            updatedParking.setParkingProperties(parkingPropertiesList);
            if (total_capacity > 0) {
                updatedParking.setTotalCapacity(BigInteger.valueOf(total_capacity));
            } else {
                updatedParking.setTotalCapacity(null);
            }
        }

        if (input.get(PARKING_AREAS) != null) {
            List<ParkingArea> parkingAreasList = resolveParkingAreasList((List) input.get(PARKING_AREAS));
            isUpdated = true;
            updatedParking.setParkingAreas(parkingAreasList);
        }

        if (input.get(VEHICLE_ENTRANCES) != null) {
            List<ParkingEntranceForVehicles> vehicleEntrancesList = resolveVehicleEntrancesList((List) input.get(VEHICLE_ENTRANCES));
            isUpdated = true;
            updatedParking.setVehicleEntrances(vehicleEntrancesList);
        }

        if (input.get(INFO_LINKS) != null) {
            List<InfoLink> infoLinksList = resolveInfoLinksList((List) input.get(INFO_LINKS));
            isUpdated = true;
            updatedParking.setInfoLinks(infoLinksList);
        }

        if (input.get(AVAILABILITY_CONDITIONS) != null) {
            List<AvailabilityCondition> availabilityConditionsList = resolveAvailabilityConditionsList((List) input.get(AVAILABILITY_CONDITIONS));
            isUpdated = true;
            updatedParking.setAvailabilityConditions(availabilityConditionsList);
        }

        Optional<PlaceEquipment> placeEquipment = placeEquipmentMapper.map(input);
        if (placeEquipment.isPresent()) {
            // Present in the input means the client intends to write it, as in SiteElementMapper.
            isUpdated = true;
            updatedParking.setPlaceEquipments(placeEquipment.get());
        }

        if (input.get(ALTERNATIVE_NAMES) != null) {
            List<AlternativeName> alternativeNames = alternativeNameMapper.mapAlternativeNames((List) input.get(ALTERNATIVE_NAMES));
            // updateAlternativeNames always replaces the stored list (matching incoming entries
            // against existing ones by name+nameType to keep their identity) — same convention
            // as availabilityConditions/vehicleEntrances above, and the same helper
            // SiteElementMapper uses for StopPlace/Quay.
            alternativeNameUpdater.updateAlternativeNames(updatedParking, alternativeNames);
            isUpdated = true;
        }

        if (input.get(ACCESSIBILITY_ASSESSMENT) != null) {
            Map<String, Object> accessibilityAssessmentInput = (Map) input.get(ACCESSIBILITY_ASSESSMENT);
            AccessibilityLimitation limitationFromInput = accessibilityLimitationMapper.map((Map<String, LimitationStatusEnumeration>) accessibilityAssessmentInput.get("limitations"));

            if (limitationFromInput != null) {
                AccessibilityAssessment accessibilityAssessment = resolveAccessibilityAssessment(limitationFromInput);
                isUpdated = true;
                updatedParking.setAccessibilityAssessment(accessibilityAssessment);
            }
        }

        return isUpdated;
    }

    private List<ParkingProperties> resolveParkingPropertiesList(List propertyList) {
        List<ParkingProperties> result = new ArrayList<>();
        for (Object property : propertyList) {
            result.add(resolveSingleParkingProperties((Map) property));
        }

        return result;
    }

    private ParkingProperties resolveSingleParkingProperties(Map input) {
        ParkingProperties p = new ParkingProperties();
        p.setSpaces(resolveParkingCapacities((List) input.get(SPACES)));
        return p;
    }

    private List<ParkingCapacity> resolveParkingCapacities(List input) {
        List<ParkingCapacity> result = new ArrayList<>();
        for (Object property : input) {
            result.add(resolveSingleParkingCapacity((Map) property));
        }

        return result;
    }

    private ParkingCapacity resolveSingleParkingCapacity(Map input) {
        ParkingCapacity capacity = new ParkingCapacity();
        capacity.setParkingUserType((ParkingUserEnumeration) input.get(PARKING_USER_TYPE));
        capacity.setParkingVehicleType((ParkingVehicleEnumeration) input.get(PARKING_VEHICLE_TYPE));
        capacity.setParkingStayType((ParkingStayEnumeration) input.get(PARKING_STAY_TYPE));
        capacity.setNumberOfSpaces((BigInteger) input.get(NUMBER_OF_SPACES));
        capacity.setNumberOfSpacesWithRechargePoint((BigInteger) input.get(NUMBER_OF_SPACES_WITH_RECHARGE_POINT));
        return capacity;
    }

    private List<ParkingArea> resolveParkingAreasList(List list) {
        List<ParkingArea> result = new ArrayList<>();
        for (Object property : list) {
            result.add(resolveSingleParkingArea((Map) property));
        }

        return result;
    }

    private ParkingArea resolveSingleParkingArea(Map input) {
        ParkingArea area = new ParkingArea();
        area.setLabel(getEmbeddableString((Map) input.get(LABEL)));
        area.setTotalCapacity((BigInteger) input.get(TOTAL_CAPACITY));
        area.setParkingProperties(resolveSingleParkingProperties((Map) input.get(PARKING_PROPERTIES)));
        return area;
    }

    private List<ParkingEntranceForVehicles> resolveVehicleEntrancesList(List list) {
        List<ParkingEntranceForVehicles> result = new ArrayList<>();
        for (Object entrance : list) {
            result.add(resolveSingleVehicleEntrance((Map) entrance));
        }
        return result;
    }

    private ParkingEntranceForVehicles resolveSingleVehicleEntrance(Map input) {
        ParkingEntranceForVehicles entrance = new ParkingEntranceForVehicles();
        Object label = input.get(LABEL);
        if (label != null) {
            entrance.setLabel(new EmbeddableMultilingualString((String) label));
        }
        entrance.setEntranceType((EntranceEnumeration) input.get(ENTRANCE_TYPE));
        entrance.setIsEntry((Boolean) input.get(IS_ENTRY));
        entrance.setIsExit((Boolean) input.get(IS_EXIT));
        entrance.setWidth(toBigDecimal(input.get(ENTRANCE_WIDTH)));
        entrance.setHeight(toBigDecimal(input.get(ENTRANCE_HEIGHT)));
        entrance.setPublicCode((String) input.get(PUBLIC_CODE));
        if (input.get(ACCESS_MODES) != null) {
            entrance.setAccessModesList((List<AccessModeEnumeration>) input.get(ACCESS_MODES));
        }
        return entrance;
    }

    /**
     * The GraphQLFloat scalar coerces input values to {@link Double}, so a plain {@code (BigDecimal)}
     * cast on the resolved map value fails; convert defensively from any {@link Number}.
     */
    private static BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return null;
    }

    private List<InfoLink> resolveInfoLinksList(List list) {
        List<InfoLink> result = new ArrayList<>();
        for (Object infoLink : list) {
            result.add(resolveSingleInfoLink((Map) infoLink));
        }
        return result;
    }

    private InfoLink resolveSingleInfoLink(Map input) {
        InfoLink infoLink = new InfoLink();
        infoLink.setUri((String) input.get(URI));
        infoLink.setTypeOfInfoLink((TypeOfInfolinkEnumeration) input.get(TYPE_OF_INFO_LINK));
        return infoLink;
    }

    /**
     * Deduplicates on the whole condition rather than on {@code dayTypeRef}: a single day type
     * legitimately carries several opening periods (e.g. 06:00–10:00 and 15:00–20:00), so only
     * a fully identical repetition is redundant and can be collapsed.
     */
    private List<AvailabilityCondition> resolveAvailabilityConditionsList(List list) {
        LinkedHashSet<AvailabilityCondition> conditions = new LinkedHashSet<>();
        for (Object conditionInput : list) {
            conditions.add(resolveSingleAvailabilityCondition((Map) conditionInput));
        }
        return new ArrayList<>(conditions);
    }

    private AvailabilityCondition resolveSingleAvailabilityCondition(Map input) {
        String dayTypeRef = (String) input.get(DAY_TYPE_REF);
        Object isAvailableObj = input.get(IS_AVAILABLE);
        boolean isAvailable = !(isAvailableObj instanceof Boolean) || (Boolean) isAvailableObj;
        LocalTime startTime = parseLocalTime((String) input.get(START_TIME));

        String endTimeValue = (String) input.get(END_TIME);
        LocalTime endTime = parseLocalTime(endTimeValue);
        int dayOffset = resolveDayOffset(input);
        if (isEndOfDay(endTimeValue)) {
            // 24:00 is shorthand for midnight at the end of the period's day.
            dayOffset = Math.max(dayOffset, 1);
        }

        if (endTime == null) {
            dayOffset = 0;
        } else if (startTime == null) {
            // NeTEx requires Timeband/StartTime and permits a start-only timeband, but never an
            // end-only one. Rejecting here stops Tiamat persisting data it can never export validly.
            throw new IllegalArgumentException(
                    "availabilityConditions entry for dayTypeRef '" + dayTypeRef
                            + "' has an endTime but no startTime; startTime is required whenever endTime is given");
        }

        return new AvailabilityCondition(dayTypeRef, isAvailable, startTime, endTime, dayOffset);
    }

    private int resolveDayOffset(Map input) {
        Object dayOffsetObj = input.get(DAY_OFFSET);
        if (!(dayOffsetObj instanceof Number dayOffsetNumber)) {
            return 0;
        }
        int dayOffset = dayOffsetNumber.intValue();
        if (dayOffset < 0) {
            throw new IllegalArgumentException("Invalid dayOffset: " + dayOffset + ". Must not be negative.");
        }
        return dayOffset;
    }

    private boolean isEndOfDay(String timeValue) {
        return "24:00".equals(timeValue) || "24:00:00".equals(timeValue);
    }

    /**
     * Accepts {@code HH:mm} or {@code HH:mm:ss}. Maps {@code 24:00}/{@code 24:00:00} to
     * midnight; callers pair that with a {@code dayOffset} of 1 so the end-of-day meaning is
     * not lost. Throws {@code IllegalArgumentException} on any invalid value so the GraphQL
     * layer returns a proper GraphQL error rather than a 500.
     */
    private LocalTime parseLocalTime(String timeValue) {
        if (timeValue == null || timeValue.isEmpty()) {
            return null;
        }
        try {
            String[] parts = timeValue.split(":");
            if (parts.length == 0 || parts[0].isEmpty()) {
                throw new IllegalArgumentException("Invalid time value: '" + timeValue + "'. Expected HH:mm or HH:mm:ss.");
            }
            int hour = Integer.parseInt(parts[0]);
            int minute = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            int second = parts.length > 2 ? Integer.parseInt(parts[2]) : 0;
            if (hour == 24 && minute == 0 && second == 0) {
                return LocalTime.MIDNIGHT;
            }
            return LocalTime.of(hour, minute, second);
        } catch (NumberFormatException | java.time.DateTimeException e) {
            throw new IllegalArgumentException("Invalid time value: '" + timeValue + "'. Expected HH:mm or HH:mm:ss.", e);
        }
    }

    private AccessibilityAssessment resolveAccessibilityAssessment(AccessibilityLimitation limitationFromInput) {
        AccessibilityAssessment accessibilityAssessment =  new AccessibilityAssessment();
        AccessibilityLimitation limitation = new AccessibilityLimitation();

        limitation.setWheelchairAccess(limitationFromInput.getWheelchairAccess());
        limitation.setAudibleSignalsAvailable(limitationFromInput.getAudibleSignalsAvailable());
        limitation.setVisualSignsAvailable(limitationFromInput.getVisualSignsAvailable());
        limitation.setStepFreeAccess(limitationFromInput.getStepFreeAccess());
        limitation.setLiftFreeAccess(limitationFromInput.getLiftFreeAccess());
        limitation.setEscalatorFreeAccess(limitationFromInput.getEscalatorFreeAccess());
        accessibilityAssessment.setLimitations(List.of(limitation));

        return accessibilityAssessment;
    }
}
