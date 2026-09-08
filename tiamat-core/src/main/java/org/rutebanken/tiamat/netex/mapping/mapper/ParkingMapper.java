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

package org.rutebanken.tiamat.netex.mapping.mapper;

import jakarta.xml.bind.JAXBElement;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypes_RelStructure;
import org.rutebanken.netex.model.InfoLinkStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingArea;
import org.rutebanken.netex.model.ParkingAreas_RelStructure;
import org.rutebanken.netex.model.ParkingEntranceForVehicles;
import org.rutebanken.netex.model.ParkingEntrancesForVehicles_RelStructure;
import org.rutebanken.netex.model.Timeband_VersionedChildStructure;
import org.rutebanken.netex.model.Timebands_RelStructure;
import org.rutebanken.netex.model.ValidityConditions_RelStructure;
import org.rutebanken.tiamat.model.InfoLink;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.model.TypeOfInfolinkEnumeration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigInteger;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

public class ParkingMapper extends CustomMapper<Parking, org.rutebanken.tiamat.model.Parking> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParkingMapper.class);
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    @Override
    public void mapAtoB(Parking parking, org.rutebanken.tiamat.model.Parking parking2, MappingContext context) {
        super.mapAtoB(parking, parking2, context);
        if (parking.getParkingAreas() != null &&
                parking.getParkingAreas().getParkingAreaRefOrParkingArea_() != null &&
                !parking.getParkingAreas().getParkingAreaRefOrParkingArea_().isEmpty()) {
            List<org.rutebanken.tiamat.model.ParkingArea> parkingAreas = mapperFacade.mapAsList(parking.getParkingAreas().getParkingAreaRefOrParkingArea_(), org.rutebanken.tiamat.model.ParkingArea.class, context);
            if (!parkingAreas.isEmpty()) {
                parking2.setParkingAreas(parkingAreas);
            }
        }
        mapPaymentMethodsFromNetex(parking, parking2);
        mapVehicleEntrancesFromNetex(parking, parking2, context);
        mapInfoLinksFromNetex(parking, parking2);
        mapAvailabilityConditionsFromNetex(parking, parking2);
    }

    @Override
    public void mapBtoA(org.rutebanken.tiamat.model.Parking tiamatParking, Parking netexParking, MappingContext context) {
        super.mapBtoA(tiamatParking, netexParking, context);
        if (tiamatParking.getParkingAreas() != null &&
                !tiamatParking.getParkingAreas().isEmpty()) {

            List<ParkingArea> parkingAreas = mapperFacade.mapAsList(tiamatParking.getParkingAreas(), ParkingArea.class, context);
            final List<JAXBElement<ParkingArea>> wrappedParkingAreas = parkingAreas.stream()
                    .map(pa -> new ObjectFactory().createParkingArea(pa))
                    .toList();

            if (!parkingAreas.isEmpty()) {
                ParkingAreas_RelStructure parkingAreas_relStructure = new ParkingAreas_RelStructure();
                parkingAreas_relStructure.getParkingAreaRefOrParkingArea_().addAll(wrappedParkingAreas);

                netexParking.setParkingAreas(parkingAreas_relStructure);
            }
        }
        mapPaymentMethodsToNetex(tiamatParking, netexParking);
        mapVehicleEntrancesToNetex(tiamatParking, netexParking, context);
        mapInfoLinksToNetex(tiamatParking, netexParking);
        mapAvailabilityConditionsToNetex(tiamatParking, netexParking);
    }

    /**
     * {@code paymentMethods} is excluded from the default Orika class map (see
     * {@code NetexMapper}) because the NeTEx and Tiamat enums are distinct types with
     * different value sets (NeTEx's is a superset). Bridge them explicitly via the
     * shared {@code value()} string, mirroring how {@code parkingType} etc. are handled
     * by name but accounting for the value-set mismatch.
     */
    private void mapPaymentMethodsFromNetex(Parking source, org.rutebanken.tiamat.model.Parking target) {
        List<org.rutebanken.netex.model.PaymentMethodEnumeration> netexMethods = source.getPaymentMethods();
        if (netexMethods == null || netexMethods.isEmpty()) {
            return;
        }
        List<PaymentMethodEnumeration> targetMethods = target.getPaymentMethods();
        targetMethods.clear();
        for (org.rutebanken.netex.model.PaymentMethodEnumeration netexMethod : netexMethods) {
            try {
                targetMethods.add(PaymentMethodEnumeration.fromValue(netexMethod.value()));
            } catch (IllegalArgumentException ignored) {
                LOGGER.warn("Parking {}: NeTEx payment method '{}' has no equivalent in the internal " +
                                "PaymentMethodEnumeration and was dropped on import.",
                        source.getId(), netexMethod.value());
            }
        }
    }

    private void mapPaymentMethodsToNetex(org.rutebanken.tiamat.model.Parking source, Parking target) {
        List<PaymentMethodEnumeration> methods = source.getPaymentMethods();
        if (methods.isEmpty()) {
            return;
        }
        List<org.rutebanken.netex.model.PaymentMethodEnumeration> targetMethods = target.getPaymentMethods();
        targetMethods.clear();
        for (PaymentMethodEnumeration method : methods) {
            try {
                targetMethods.add(org.rutebanken.netex.model.PaymentMethodEnumeration.fromValue(method.value()));
            } catch (IllegalArgumentException ignored) {
                LOGGER.warn("Parking {}: internal payment method '{}' has no equivalent in the NeTEx " +
                                "PaymentMethodEnumeration and was dropped on export.",
                        source.getNetexId(), method.value());
            }
        }
    }

    /**
     * {@code vehicleEntrances} is excluded from the default Orika class map (see
     * {@code NetexMapper}) because NeTEx models it as a {@code RelStructure} (a wrapper
     * holding refs-or-entries) while Tiamat persists a plain {@code List}. Orika cannot
     * bridge that structural mismatch on its own.
     */
    private void mapVehicleEntrancesFromNetex(Parking source, org.rutebanken.tiamat.model.Parking target, MappingContext context) {
        if (source.getVehicleEntrances() == null
                || source.getVehicleEntrances().getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles().isEmpty()) {
            return;
        }
        List<ParkingEntranceForVehicles> netexEntrances = source.getVehicleEntrances()
                .getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles().stream()
                .filter(ParkingEntranceForVehicles.class::isInstance)
                .map(ParkingEntranceForVehicles.class::cast)
                .toList();

        List<org.rutebanken.tiamat.model.ParkingEntranceForVehicles> entrances =
                mapperFacade.mapAsList(netexEntrances, org.rutebanken.tiamat.model.ParkingEntranceForVehicles.class, context);
        if (!entrances.isEmpty()) {
            mapAccessModesFromNetex(netexEntrances, entrances);
            target.setVehicleEntrances(entrances);
        }
    }

    private void mapVehicleEntrancesToNetex(org.rutebanken.tiamat.model.Parking source, Parking target, MappingContext context) {
        if (source.getVehicleEntrances() == null || source.getVehicleEntrances().isEmpty()) {
            return;
        }
        List<ParkingEntranceForVehicles> entrances = mapperFacade.mapAsList(
                source.getVehicleEntrances(), ParkingEntranceForVehicles.class, context);
        if (!entrances.isEmpty()) {
            mapAccessModesToNetex(source.getVehicleEntrances(), entrances);
            ParkingEntrancesForVehicles_RelStructure rel = new ParkingEntrancesForVehicles_RelStructure();
            rel.getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles().addAll(entrances);
            target.setVehicleEntrances(rel);
        }
    }

    /**
     * {@code accessModes} is excluded from Orika's default entrance mapping because the
     * NeTEx side is a {@code List<AccessModeEnumeration>} (repeatable XML element) while
     * the Tiamat side is stored as a single space-separated token string (see
     * {@code ParkingEntranceForVehicles.getAccessModesList()}). Bridge both edges
     * explicitly, in matching list order, mirroring {@link #mapPaymentMethodsFromNetex}.
     */
    private void mapAccessModesFromNetex(List<ParkingEntranceForVehicles> netexEntrances,
                                          List<org.rutebanken.tiamat.model.ParkingEntranceForVehicles> entrances) {
        for (int i = 0; i < netexEntrances.size() && i < entrances.size(); i++) {
            List<org.rutebanken.netex.model.AccessModeEnumeration> netexAccessModes = netexEntrances.get(i).getAccessModes();
            if (netexAccessModes == null || netexAccessModes.isEmpty()) {
                continue;
            }
            List<org.rutebanken.tiamat.model.AccessModeEnumeration> accessModes = netexAccessModes.stream()
                    .map(mode -> {
                        try {
                            return org.rutebanken.tiamat.model.AccessModeEnumeration.fromValue(mode.value());
                        } catch (IllegalArgumentException ignored) {
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();
            entrances.get(i).setAccessModesList(accessModes);
        }
    }

    private void mapAccessModesToNetex(List<org.rutebanken.tiamat.model.ParkingEntranceForVehicles> source,
                                        List<ParkingEntranceForVehicles> entrances) {
        for (int i = 0; i < source.size() && i < entrances.size(); i++) {
            List<org.rutebanken.tiamat.model.AccessModeEnumeration> accessModes = source.get(i).getAccessModesList();
            if (accessModes.isEmpty()) {
                continue;
            }
            List<org.rutebanken.netex.model.AccessModeEnumeration> netexAccessModes = accessModes.stream()
                    .map(mode -> {
                        try {
                            return org.rutebanken.netex.model.AccessModeEnumeration.fromValue(mode.value());
                        } catch (IllegalArgumentException ignored) {
                            return null;
                        }
                    })
                    .filter(java.util.Objects::nonNull)
                    .toList();
            if (!netexAccessModes.isEmpty()) {
                entrances.get(i).withAccessModes(netexAccessModes);
            }
        }
    }

    /**
     * {@code infoLinks} is excluded from Orika's default classmap (see {@code NetexMapper})
     * because it is declared {@code @Transient} on the shared ancestor
     * {@code GroupOfEntities_VersionStructure} and only shadowed as persisted on
     * {@code Parking} — bridge it explicitly, mirroring {@link #mapPaymentMethodsFromNetex}.
     * Only the first {@code typeOfInfoLink} value is kept (NeTEx's list-typed attribute is
     * never populated with more than one value by any producer we integrate).
     */
    private void mapInfoLinksFromNetex(Parking source, org.rutebanken.tiamat.model.Parking target) {
        if (source.getInfoLinks() == null || source.getInfoLinks().getInfoLink().isEmpty()) {
            return;
        }
        List<InfoLink> infoLinks = new ArrayList<>();
        for (InfoLinkStructure netexLink : source.getInfoLinks().getInfoLink()) {
            if (netexLink.getValue() == null || netexLink.getValue().isBlank()) {
                continue;
            }
            TypeOfInfolinkEnumeration type = null;
            List<org.rutebanken.netex.model.TypeOfInfolinkEnumeration> types = netexLink.getTypeOfInfoLink();
            if (types != null && !types.isEmpty()) {
                try {
                    type = TypeOfInfolinkEnumeration.fromValue(types.get(0).value());
                } catch (IllegalArgumentException ignored) {
                    // unknown value — leave type unset
                }
            }
            infoLinks.add(new InfoLink(netexLink.getValue(), type));
        }
        if (!infoLinks.isEmpty()) {
            target.setInfoLinks(infoLinks);
        }
    }

    private void mapInfoLinksToNetex(org.rutebanken.tiamat.model.Parking source, Parking target) {
        List<InfoLink> infoLinks = source.getInfoLinks();
        if (infoLinks.isEmpty()) {
            return;
        }
        org.rutebanken.netex.model.GroupOfEntities_VersionStructure.InfoLinks relStruct =
                new org.rutebanken.netex.model.GroupOfEntities_VersionStructure.InfoLinks();
        for (InfoLink infoLink : infoLinks) {
            InfoLinkStructure netexLink = new InfoLinkStructure();
            netexLink.setValue(infoLink.getUri());
            if (infoLink.getTypeOfInfoLink() != null) {
                try {
                    netexLink.getTypeOfInfoLink().add(
                            org.rutebanken.netex.model.TypeOfInfolinkEnumeration.fromValue(infoLink.getTypeOfInfoLink().value()));
                } catch (IllegalArgumentException ignored) {
                    // stored value no longer valid — skip type
                }
            }
            relStruct.getInfoLink().add(netexLink);
        }
        target.setInfoLinks(relStruct);
    }

    /**
     * {@code availabilityConditions} has no matching-named NeTEx accessor (NeTEx's field is
     * {@code validityConditions}, already bridged for the singular {@code validBetween} via
     * Orika's {@code validBetween[0]} field mapping and {@code ValidBetweenConverter}) — no
     * {@code .exclude(...)} is needed in {@code NetexMapper}.
     * <p>
     * A source {@code AvailabilityCondition} may carry several {@code DayTypeRef}s and several
     * inline {@code Timeband}s. That is semantically equivalent to one condition per
     * (day type, timeband) combination, so each combination is expanded into its own
     * {@link org.rutebanken.tiamat.model.AvailabilityCondition} rather than the surplus being
     * discarded. Exact duplicates are collapsed so that re-importing the same document is
     * idempotent.
     */
    private void mapAvailabilityConditionsFromNetex(Parking source, org.rutebanken.tiamat.model.Parking target) {
        ValidityConditions_RelStructure validityConditions = source.getValidityConditions();
        if (validityConditions == null) {
            return;
        }

        LinkedHashSet<org.rutebanken.tiamat.model.AvailabilityCondition> conditions = new LinkedHashSet<>();
        for (Object entry : validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_()) {
            if (!(entry instanceof JAXBElement<?> jaxbElement)) {
                continue;
            }
            if (!(jaxbElement.getValue() instanceof AvailabilityCondition availabilityCondition)) {
                continue;
            }

            List<String> dayTypeRefs = extractDayTypeRefs(availabilityCondition);
            if (dayTypeRefs.isEmpty()) {
                continue;
            }

            boolean isAvailable = availabilityCondition.isIsAvailable() == null || availabilityCondition.isIsAvailable();
            List<Timeband_VersionedChildStructure> timebands = extractInlineTimebands(availabilityCondition);

            for (String dayTypeRef : dayTypeRefs) {
                if (timebands.isEmpty()) {
                    conditions.add(new org.rutebanken.tiamat.model.AvailabilityCondition(
                            dayTypeRef, isAvailable, null, null, 0));
                    continue;
                }
                for (Timeband_VersionedChildStructure timeband : timebands) {
                    LocalTime startTime = timeband.getStartTime();
                    LocalTime endTime = timeband.getEndTime();
                    if (startTime == null && endTime != null) {
                        // NeTEx requires Timeband/StartTime (netex_dayType_version.xsd,
                        // TimeSpanGroup), so an end-only timeband is schema-invalid. Accepting it
                        // would let Tiamat persist data it can never export validly.
                        throw new IllegalArgumentException("Parking " + source.getId()
                                + " AvailabilityCondition for dayTypeRef '" + dayTypeRef
                                + "' has a Timeband with an EndTime but no StartTime");
                    }
                    int dayOffset = timeband.getDayOffset() == null ? 0 : timeband.getDayOffset().intValue();
                    conditions.add(new org.rutebanken.tiamat.model.AvailabilityCondition(
                            dayTypeRef, isAvailable, startTime, endTime, dayOffset));
                }
            }
        }

        if (!conditions.isEmpty()) {
            target.setAvailabilityConditions(new ArrayList<>(conditions));
        }
    }

    private List<String> extractDayTypeRefs(AvailabilityCondition availabilityCondition) {
        DayTypes_RelStructure dayTypes = availabilityCondition.getDayTypes();
        if (dayTypes == null) {
            return List.of();
        }
        List<String> refs = new ArrayList<>();
        for (JAXBElement<?> dayTypeEntry : dayTypes.getDayTypeRefOrDayType_()) {
            if (dayTypeEntry.getValue() instanceof DayTypeRefStructure ref && ref.getRef() != null) {
                refs.add(ref.getRef());
            }
        }
        return refs;
    }

    /**
     * {@code Timebands_RelStructure.timebandRefOrTimeband} is {@code @XmlElements}
     * (type-matched, not {@code JAXBElement}-wrapped), so a schema-valid inline timeband is a
     * raw {@code Timeband_VersionedChildStructure}. A {@code JAXBElement}-wrapped value is also
     * accepted defensively, in case some producer wraps it non-standardly.
     */
    private List<Timeband_VersionedChildStructure> extractInlineTimebands(AvailabilityCondition availabilityCondition) {
        Timebands_RelStructure timebands = availabilityCondition.getTimebands();
        if (timebands == null) {
            return List.of();
        }

        List<Timeband_VersionedChildStructure> inlineTimebands = new ArrayList<>();
        for (Object timebandEntry : timebands.getTimebandRefOrTimeband()) {
            if (timebandEntry instanceof Timeband_VersionedChildStructure raw) {
                inlineTimebands.add(raw);
            } else if (timebandEntry instanceof JAXBElement<?> timebandJaxb
                    && timebandJaxb.getValue() instanceof Timeband_VersionedChildStructure wrapped) {
                inlineTimebands.add(wrapped);
            }
        }
        return inlineTimebands;
    }

    /**
     * The NeTEx export pipeline maps the same {@code Parking} to NeTEx more than once per
     * export (e.g. once per frame that embeds it). Unlike {@code vehicleEntrances} (overwritten
     * via a plain setter), this method appends to a list that may already carry an existing
     * {@code ValidBetween} entry from Orika's own {@code validBetween[0]} mapping, so a repeat
     * invocation must remove only the {@code AvailabilityCondition} entries it previously added
     * itself — otherwise the second invocation duplicates them with identical ids, violating
     * NeTEx's {@code ValidityCondition_AnyVersionedKey} uniqueness constraint on export.
     */
    private void mapAvailabilityConditionsToNetex(org.rutebanken.tiamat.model.Parking source, Parking target) {
        List<org.rutebanken.tiamat.model.AvailabilityCondition> conditions = source.getAvailabilityConditions();
        if (conditions.isEmpty()) {
            return;
        }

        ValidityConditions_RelStructure validityConditions = target.getValidityConditions();
        if (validityConditions == null) {
            validityConditions = new ValidityConditions_RelStructure();
            target.setValidityConditions(validityConditions);
        }
        List<Object> validityConditionEntries = validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_();
        validityConditionEntries.removeIf(entry -> entry instanceof JAXBElement<?> jaxbElement
                && jaxbElement.getValue() instanceof AvailabilityCondition);

        int index = 1;
        for (org.rutebanken.tiamat.model.AvailabilityCondition condition : conditions) {
            AvailabilityCondition availabilityCondition = new AvailabilityCondition()
                    .withId(source.getNetexId() + ":AvailabilityCondition:" + index)
                    .withVersion("1")
                    .withIsAvailable(condition.isAvailable());

            DayTypeRefStructure dayTypeRef = new DayTypeRefStructure().withRef(condition.getDayTypeRef());
            DayTypes_RelStructure dayTypes = new DayTypes_RelStructure();
            dayTypes.getDayTypeRefOrDayType_().add(OBJECT_FACTORY.createDayTypeRef(dayTypeRef));
            availabilityCondition.withDayTypes(dayTypes);

            if (condition.getStartTime() != null) {
                // NeTEx requires Timeband/StartTime and permits a start-only timeband ("ends at
                // the end of day"), but never an end-only one — so a timeband is emitted on the
                // presence of startTime alone, never on endTime.
                // See extractInlineTimebands javadoc: must be a raw Timeband_VersionedChildStructure,
                // not the named Timeband subtype and not JAXBElement-wrapped, or NeTEx export crashes
                // trying to marshal a substitute for this anonymous XSD type.
                Timeband_VersionedChildStructure timeband = new Timeband_VersionedChildStructure()
                        .withId(source.getNetexId() + ":Timeband:" + index)
                        .withVersion("1")
                        .withStartTime(condition.getStartTime())
                        .withEndTime(condition.getEndTime());
                if (condition.getEndTime() != null && condition.getDayOffset() != 0) {
                    timeband.setDayOffset(BigInteger.valueOf(condition.getDayOffset()));
                }
                Timebands_RelStructure timebands = new Timebands_RelStructure();
                timebands.getTimebandRefOrTimeband().add(timeband);
                availabilityCondition.withTimebands(timebands);
            }

            validityConditionEntries.add(OBJECT_FACTORY.createAvailabilityCondition(availabilityCondition));
            index++;
        }
    }
}
