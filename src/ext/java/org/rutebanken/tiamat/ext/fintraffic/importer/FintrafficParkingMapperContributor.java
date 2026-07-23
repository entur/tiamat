package org.rutebanken.tiamat.ext.fintraffic.importer;

import ma.glasnost.orika.MappingContext;
import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.AvailabilityCondition;
import org.rutebanken.netex.model.DayTypeRefStructure;
import org.rutebanken.netex.model.DayTypes_RelStructure;
import org.rutebanken.netex.model.EntranceEnumeration;
import org.rutebanken.netex.model.InfoLinkStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.ParkingEntranceForVehicles;
import org.rutebanken.netex.model.ParkingEntrancesForVehicles_RelStructure;
import org.rutebanken.netex.model.Timeband;
import org.rutebanken.netex.model.Timebands_RelStructure;
import org.rutebanken.netex.model.ValidityConditions_RelStructure;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingAvailabilityCondition;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficInfoLink;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingEntranceForVehicles;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.ParkingMapperContributor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Fintraffic extension of {@link ParkingMapperContributor} that wires
 * {@code paymentMethods} and {@code infoLinks} through the NeTEx ↔ Tiamat
 * mapping in both directions.
 *
 * <p><b>Import ({@link #mapFromNetex}):</b> copies the NeTEx fields onto the
 * {@link FintrafficParking} so that the
 * {@link FintrafficMergingParkingImporter#mergeExtendedFields} hook can persist them.
 *
 * <p><b>Export ({@link #mapToNetex}):</b> reads the persisted fields from a
 * {@link FintrafficParking} and writes them back into the NeTEx output so that the
 * fields survive the export roundtrip.
 */
@Profile("fintraffic")
@Component
public class FintrafficParkingMapperContributor implements ParkingMapperContributor {

    private static final Logger logger = LoggerFactory.getLogger(FintrafficParkingMapperContributor.class);
    private static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

    @Override
    public void mapFromNetex(org.rutebanken.netex.model.Parking source,
                             org.rutebanken.tiamat.model.Parking target,
                             MappingContext context) {
        mapPaymentMethodsFromNetex(source, target);
        mapInfoLinksFromNetex(source, target);
        mapVehicleEntrancesFromNetex(source, target);
        mapAvailabilityConditionsFromNetex(source, target);
    }

    @Override
    public void mapToNetex(org.rutebanken.tiamat.model.Parking source,
                           org.rutebanken.netex.model.Parking target,
                           MappingContext context) {
        if (!(source instanceof FintrafficParking fp)) {
            return;
        }
        mapPaymentMethodsToNetex(fp, target);
        mapInfoLinksToNetex(fp, target);
        mapVehicleEntrancesToNetex(fp, target);
        mapAvailabilityConditionsToNetex(fp, target);
    }

    // --- paymentMethods ---

    private void mapPaymentMethodsFromNetex(org.rutebanken.netex.model.Parking source,
                                             org.rutebanken.tiamat.model.Parking target) {
        var netexMethods = source.getPaymentMethods();
        if (netexMethods == null || netexMethods.isEmpty()) {
            return;
        }
        var targetMethods = target.getPaymentMethods();
        targetMethods.clear();
        for (var netexMethod : netexMethods) {
            try {
                targetMethods.add(PaymentMethodEnumeration.fromValue(netexMethod.value()));
            } catch (IllegalArgumentException ignored) {
                // skip unknown values
            }
        }
    }

    private void mapPaymentMethodsToNetex(FintrafficParking source,
                                          org.rutebanken.netex.model.Parking target) {
        var methods = source.getPaymentMethods();
        if (methods.isEmpty()) {
            return;
        }
        var targetMethods = target.getPaymentMethods();
        targetMethods.clear();
        for (var method : methods) {
            try {
                targetMethods.add(org.rutebanken.netex.model.PaymentMethodEnumeration.fromValue(method.value()));
            } catch (IllegalArgumentException ignored) {
                // skip unknown values
            }
        }
    }

    // --- infoLinks ---

    private void mapInfoLinksFromNetex(org.rutebanken.netex.model.Parking source,
                                        org.rutebanken.tiamat.model.Parking target) {
        if (!(target instanceof FintrafficParking fp)) {
            return;
        }
        var infoLinksRelStruct = source.getInfoLinks();
        if (infoLinksRelStruct == null) {
            return;
        }
        List<InfoLinkStructure> netexLinks = infoLinksRelStruct.getInfoLink();
        if (netexLinks == null || netexLinks.isEmpty()) {
            return;
        }

        List<FintrafficInfoLink> converted = new ArrayList<>();
        for (InfoLinkStructure link : netexLinks) {
            if (link.getValue() == null || link.getValue().isBlank()) {
                continue;
            }
            String typeValue = null;
            var types = link.getTypeOfInfoLink();
            if (types != null && !types.isEmpty()) {
                typeValue = types.getFirst().value();
            }
            converted.add(new FintrafficInfoLink(link.getValue(), typeValue));
        }
        fp.setInfoLinks(converted);
    }

    private void mapInfoLinksToNetex(FintrafficParking source,
                                      org.rutebanken.netex.model.Parking target) {
        var links = source.getInfoLinks();
        if (links.isEmpty()) {
            return;
        }

        var relStruct = new org.rutebanken.netex.model.GroupOfEntities_VersionStructure.InfoLinks();
        for (FintrafficInfoLink link : links) {
            InfoLinkStructure netexLink = new InfoLinkStructure();
            netexLink.setValue(link.getUri());
            if (link.getTypeOfInfoLink() != null) {
                try {
                    netexLink.getTypeOfInfoLink().add(
                            org.rutebanken.netex.model.TypeOfInfolinkEnumeration.fromValue(link.getTypeOfInfoLink()));
                } catch (IllegalArgumentException ignored) {
                    // stored value no longer valid — skip type
                }
            }
            relStruct.getInfoLink().add(netexLink);
        }
        target.setInfoLinks(relStruct);
    }

    // --- vehicleEntrances ---

    private void mapVehicleEntrancesFromNetex(org.rutebanken.netex.model.Parking source,
                                               org.rutebanken.tiamat.model.Parking target) {
        if (!(target instanceof FintrafficParking fp)) {
            return;
        }
        ParkingEntrancesForVehicles_RelStructure relStruct = source.getVehicleEntrances();
        if (relStruct == null) {
            return;
        }
        List<Object> items = relStruct.getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles();
        if (items == null || items.isEmpty()) {
            return;
        }

        List<FintrafficParkingEntranceForVehicles> converted = new ArrayList<>();
        for (Object item : items) {
            if (!(item instanceof ParkingEntranceForVehicles entrance)) {
                continue;
            }
            String label = entrance.getLabel() != null ? entrance.getLabel().getValue() : null;
            String entranceType = entrance.getEntranceType() != null ? entrance.getEntranceType().value() : null;
            converted.add(new FintrafficParkingEntranceForVehicles(
                    label,
                    entranceType,
                    entrance.getWidth(),
                    entrance.getHeight(),
                    entrance.isIsEntry(),
                    entrance.isIsExit(),
                    entrance.getPublicCode()
            ));
        }
        fp.setFintrafficVehicleEntrances(converted);
    }

    private void mapVehicleEntrancesToNetex(FintrafficParking source,
                                             org.rutebanken.netex.model.Parking target) {
        var entrances = source.getFintrafficVehicleEntrances();
        if (entrances.isEmpty()) {
            return;
        }

        ParkingEntrancesForVehicles_RelStructure relStruct = new ParkingEntrancesForVehicles_RelStructure();
        for (FintrafficParkingEntranceForVehicles entrance : entrances) {
            ParkingEntranceForVehicles netexEntrance = new ParkingEntranceForVehicles();
            if (entrance.getLabel() != null) {
                netexEntrance.setLabel(new org.rutebanken.netex.model.MultilingualString().withValue(entrance.getLabel()));
            }
            if (entrance.getEntranceType() != null) {
                try {
                    netexEntrance.setEntranceType(EntranceEnumeration.fromValue(entrance.getEntranceType()));
                } catch (IllegalArgumentException ignored) {
                    // stored value no longer valid — skip type
                }
            }
            netexEntrance.setWidth(entrance.getWidth());
            netexEntrance.setHeight(entrance.getHeight());
            netexEntrance.setIsEntry(entrance.getIsEntry());
            netexEntrance.setIsExit(entrance.getIsExit());
            netexEntrance.setPublicCode(entrance.getPublicCode());
            relStruct.getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles().add(netexEntrance);
        }
        target.setVehicleEntrances(relStruct);
    }

    // --- availabilityConditions ---

    private void mapAvailabilityConditionsFromNetex(org.rutebanken.netex.model.Parking source,
                                                    org.rutebanken.tiamat.model.Parking target) {
        if (!(target instanceof FintrafficParking fp)) {
            return;
        }
        var validityConditions = source.getValidityConditions();
        if (validityConditions == null) {
            return;
        }

        LinkedHashMap<String, FintrafficParkingAvailabilityCondition> byDayType = new LinkedHashMap<>();
        for (Object entry : validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_()) {
            if (!(entry instanceof JAXBElement<?> jaxbElement)) {
                continue;
            }
            if (!(jaxbElement.getValue() instanceof AvailabilityCondition availabilityCondition)) {
                continue;
            }

            String dayTypeRef = extractDayTypeRef(source, availabilityCondition);
            if (dayTypeRef == null) {
                continue;
            }

            LocalTime startTime = null;
            LocalTime endTime = null;
            Timeband inlineTimeband = extractInlineTimeband(source, availabilityCondition);
            if (inlineTimeband != null) {
                startTime = inlineTimeband.getStartTime();
                endTime = inlineTimeband.getEndTime();
            }

            boolean isAvailable = availabilityCondition.isIsAvailable() == null || availabilityCondition.isIsAvailable();
            if (byDayType.containsKey(dayTypeRef)) {
                logger.warn("Parking {} has duplicate AvailabilityCondition for dayTypeRef '{}'; keeping the last one",
                        source.getId(), dayTypeRef);
            }
            byDayType.put(dayTypeRef, new FintrafficParkingAvailabilityCondition(dayTypeRef, isAvailable, startTime, endTime));
        }

        fp.setAvailabilityConditions(new ArrayList<>(byDayType.values()));
    }

    private String extractDayTypeRef(org.rutebanken.netex.model.Parking source, AvailabilityCondition availabilityCondition) {
        DayTypes_RelStructure dayTypes = availabilityCondition.getDayTypes();
        if (dayTypes == null || dayTypes.getDayTypeRefOrDayType_().isEmpty()) {
            return null;
        }
        if (dayTypes.getDayTypeRefOrDayType_().size() > 1) {
            logger.warn("Parking {} AvailabilityCondition has {} dayTypes; using the first DayTypeRef only",
                    source.getId(), dayTypes.getDayTypeRefOrDayType_().size());
        }
        for (JAXBElement<?> dayTypeEntry : dayTypes.getDayTypeRefOrDayType_()) {
            if (dayTypeEntry.getValue() instanceof DayTypeRefStructure ref) {
                return ref.getRef();
            }
        }
        return null;
    }

    private Timeband extractInlineTimeband(org.rutebanken.netex.model.Parking source, AvailabilityCondition availabilityCondition) {
        Timebands_RelStructure timebands = availabilityCondition.getTimebands();
        if (timebands == null || timebands.getTimebandRefOrTimeband().isEmpty()) {
            return null;
        }

        Timeband firstInlineTimeband = null;
        int inlineTimebandCount = 0;
        for (Object timebandEntry : timebands.getTimebandRefOrTimeband()) {
            if (timebandEntry instanceof JAXBElement<?> timebandJaxb
                    && timebandJaxb.getValue() instanceof Timeband timeband) {
                inlineTimebandCount++;
                if (firstInlineTimeband == null) {
                    firstInlineTimeband = timeband;
                }
            }
        }

        if (inlineTimebandCount > 1) {
            logger.warn("Parking {} AvailabilityCondition has {} inline timebands; using the first only",
                    source.getId(), inlineTimebandCount);
        }

        return firstInlineTimeband;
    }

    private void mapAvailabilityConditionsToNetex(FintrafficParking source,
                                                  org.rutebanken.netex.model.Parking target) {
        var conditions = source.getAvailabilityConditions();
        if (conditions.isEmpty()) {
            return;
        }

        ValidityConditions_RelStructure validityConditions = target.getValidityConditions();
        if (validityConditions == null) {
            validityConditions = new ValidityConditions_RelStructure();
            target.setValidityConditions(validityConditions);
        }
        List<Object> validityConditionEntries = validityConditions.getValidityConditionRefOrValidBetweenOrValidityCondition_();

        int index = 1;
        for (FintrafficParkingAvailabilityCondition condition : conditions) {
            AvailabilityCondition availabilityCondition = new AvailabilityCondition()
                    .withId(source.getId() + ":AvailabilityCondition:" + index)
                    .withVersion("1")
                    .withIsAvailable(condition.isAvailable());

            DayTypeRefStructure dayTypeRef = new DayTypeRefStructure().withRef(condition.getDayTypeRef());
            DayTypes_RelStructure dayTypes = new DayTypes_RelStructure();
            dayTypes.getDayTypeRefOrDayType_().add(OBJECT_FACTORY.createDayTypeRef(dayTypeRef));
            availabilityCondition.withDayTypes(dayTypes);

            if (condition.getStartTime() != null || condition.getEndTime() != null) {
                Timeband timeband = new Timeband()
                        .withId(source.getId() + ":Timeband:" + index)
                        .withVersion("1")
                        .withStartTime(condition.getStartTime())
                        .withEndTime(condition.getEndTime());
                Timebands_RelStructure timebands = new Timebands_RelStructure();
                timebands.getTimebandRefOrTimeband().add(OBJECT_FACTORY.createTimeband(timeband));
                availabilityCondition.withTimebands(timebands);
            }

            validityConditionEntries.add(OBJECT_FACTORY.createAvailabilityCondition(availabilityCondition));
            index++;
        }
    }
}
