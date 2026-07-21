package org.rutebanken.tiamat.ext.fintraffic.rest.graphql;

import graphql.schema.DataFetchingEnvironment;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficInfoLink;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingAvailabilityCondition;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingEntranceForVehicles;
import org.rutebanken.tiamat.model.LightingEnumeration;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.rest.graphql.fetchers.ParkingUpdater;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.AVAILABILITY_CONDITIONS;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.DAY_TYPE_REF;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.END_TIME;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.IS_AVAILABLE;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.INFO_LINKS;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.LIGHTING;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.PAYMENT_METHODS;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.TYPE_OF_INFO_LINK;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.URI;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.VEHICLE_ENTRANCES;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.ENTRANCE_TYPE;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.VEHICLE_ENTRANCE_LABEL;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.WIDTH;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.HEIGHT;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.IS_ENTRY;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.IS_EXIT;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.PUBLIC_CODE;
import static org.rutebanken.tiamat.ext.fintraffic.rest.graphql.FintrafficParkingGraphQLTypeContributor.START_TIME;

/**
 * Fintraffic extension of {@link ParkingUpdater} that handles the
 * {@code paymentMethods} and {@code infoLinks} input fields contributed by
 * {@link FintrafficParkingGraphQLTypeContributor}.
 */
@Profile("fintraffic")
@Transactional
public class FintrafficParkingUpdater extends ParkingUpdater {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Extends the parent's {@code get()} to flush pending collection inserts and
     * refresh entities before the transaction closes.
     */
    @SuppressWarnings("unchecked")
    @Override
    public Object get(DataFetchingEnvironment environment) {
        List<Parking> parkings = (List<Parking>) super.get(environment);
        if (parkings != null) {
            entityManager.flush();
            parkings.stream()
                    .filter(Objects::nonNull)
                    .filter(entityManager::contains)
                    .forEach(entityManager::refresh);
        }
        return parkings;
    }

    @Override
    protected boolean populateExtendedFields(Map input, Parking parking) {
        if (!(parking instanceof FintrafficParking target)) {
            return false;
        }

        boolean changed = false;

        LightingEnumeration lighting = (LightingEnumeration) input.get(LIGHTING);
        if (lighting != null && !lighting.equals(target.getLighting())) {
            target.setLighting(lighting);
            changed = true;
        }

        @SuppressWarnings("unchecked")
        List<PaymentMethodEnumeration> incomingMethods = (List<PaymentMethodEnumeration>) input.get(PAYMENT_METHODS);
        if (incomingMethods != null && !incomingMethods.equals(target.getPaymentMethods())) {
            target.setPaymentMethods(List.copyOf(incomingMethods));
            changed = true;
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> incomingLinks = (List<Map<String, Object>>) input.get(INFO_LINKS);
        if (incomingLinks != null) {
            List<FintrafficInfoLink> converted = new ArrayList<>();
            for (Map<String, Object> linkInput : incomingLinks) {
                Object uriObj = linkInput.get(URI);
                if (uriObj == null) {
                    continue;
                }
                String uri = uriObj.toString();
                Object typeObj = linkInput.get(TYPE_OF_INFO_LINK);
                String type = null;
                if (typeObj instanceof org.rutebanken.netex.model.TypeOfInfolinkEnumeration enumVal) {
                    type = enumVal.value();
                } else if (typeObj != null) {
                    type = typeObj.toString();
                }
                converted.add(new FintrafficInfoLink(uri, type));
            }
            if (!converted.equals(target.getInfoLinks())) {
                target.setInfoLinks(converted);
                changed = true;
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> incomingEntrances = (List<Map<String, Object>>) input.get(VEHICLE_ENTRANCES);
        if (incomingEntrances != null) {
            List<FintrafficParkingEntranceForVehicles> converted = new ArrayList<>();
            for (Map<String, Object> entranceInput : incomingEntrances) {
                Object labelObj = entranceInput.get(VEHICLE_ENTRANCE_LABEL);
                Object typeObj = entranceInput.get(ENTRANCE_TYPE);
                Object widthObj = entranceInput.get(WIDTH);
                Object heightObj = entranceInput.get(HEIGHT);
                Object isEntryObj = entranceInput.get(IS_ENTRY);
                Object isExitObj = entranceInput.get(IS_EXIT);
                Object publicCodeObj = entranceInput.get(PUBLIC_CODE);

                String entranceTypeStr = null;
                if (typeObj instanceof org.rutebanken.netex.model.EntranceEnumeration enumVal) {
                    entranceTypeStr = enumVal.value();
                } else if (typeObj != null) {
                    entranceTypeStr = typeObj.toString();
                }

                converted.add(new FintrafficParkingEntranceForVehicles(
                        labelObj != null ? labelObj.toString() : null,
                        entranceTypeStr,
                        toBigDecimal(widthObj),
                        toBigDecimal(heightObj),
                        isEntryObj instanceof Boolean b ? b : null,
                        isExitObj instanceof Boolean b ? b : null,
                        publicCodeObj != null ? publicCodeObj.toString() : null
                ));
            }
            if (!converted.equals(target.getFintrafficVehicleEntrances())) {
                target.setFintrafficVehicleEntrances(converted);
                changed = true;
            }
        }

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> incomingConditions = (List<Map<String, Object>>) input.get(AVAILABILITY_CONDITIONS);
        if (incomingConditions != null) {
            LinkedHashMap<String, FintrafficParkingAvailabilityCondition> byDayType = new LinkedHashMap<>();
            for (Map<String, Object> conditionInput : incomingConditions) {
                Object dayTypeRefObj = conditionInput.get(DAY_TYPE_REF);
                if (dayTypeRefObj == null) {
                    continue;
                }
                String dayTypeRef = dayTypeRefObj.toString();
                if (byDayType.containsKey(dayTypeRef)) {
                    throw new IllegalArgumentException(
                            "Duplicate dayTypeRef '" + dayTypeRef + "' in availabilityConditions input");
                }
                Object isAvailableObj = conditionInput.get(IS_AVAILABLE);
                Object startTimeObj = conditionInput.get(START_TIME);
                Object endTimeObj = conditionInput.get(END_TIME);

                boolean isAvailable = !(isAvailableObj instanceof Boolean b) || b;
                LocalTime startTime = parseLocalTime(startTimeObj);
                LocalTime endTime = parseLocalTime(endTimeObj);

                byDayType.put(dayTypeRef, new FintrafficParkingAvailabilityCondition(
                        dayTypeRef,
                        isAvailable,
                        startTime,
                        endTime
                ));
            }
            List<FintrafficParkingAvailabilityCondition> converted = new ArrayList<>(byDayType.values());
            if (!converted.equals(target.getAvailabilityConditions())) {
                target.setAvailabilityConditions(converted);
                changed = true;
            }
        }

        return changed;
    }

    /**
     * Copies extended fields that Orika does not transfer from the existing version into
     * the newly created version copy.  Called by the parent's update path immediately
     * after {@link org.rutebanken.tiamat.versioning.VersionCreator#createCopy}, before
     * {@link #populateExtendedFields} overwrites them with the GraphQL input values.
     */
    @Override
    protected void preserveExtendedFields(Parking existingVersion, Parking copy) {
        if (existingVersion instanceof FintrafficParking source && copy instanceof FintrafficParking target) {
            target.setLighting(source.getLighting());
            target.setPaymentMethods(new ArrayList<>(source.getPaymentMethods()));
            target.setInfoLinks(new ArrayList<>(source.getInfoLinks()));
            target.setFintrafficVehicleEntrances(new ArrayList<>(source.getFintrafficVehicleEntrances()));
            target.setAvailabilityConditions(new ArrayList<>(source.getAvailabilityConditions()));
        }
    }

    private static BigDecimal toBigDecimal(Object value) {
        if (value instanceof BigDecimal bd) {
            return bd;
        }
        if (value instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }
        return null;
    }

    private static LocalTime parseLocalTime(Object value) {
        if (value == null) {
            return null;
        }
        String timeValue = value.toString().strip();
        if (timeValue.isEmpty()) {
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
}
