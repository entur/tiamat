package org.rutebanken.tiamat.exporter;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.EntityInVersionStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PassengerStopAssignment;
import org.rutebanken.netex.model.QuayRefStructure;
import org.rutebanken.netex.model.ScheduledStopPoint;
import org.rutebanken.netex.model.ScheduledStopPointRefStructure;
import org.rutebanken.netex.model.StopPlaceRefStructure;
import org.rutebanken.netex.model.ValidBetween;
import org.rutebanken.tiamat.model.Quay;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class ServiceFrameElementCreator {
    private final NetexIdHelper netexIdHelper;
    private final ObjectFactory objectFactory = new ObjectFactory();

    public ServiceFrameElementCreator(NetexIdHelper netexIdHelper) {
        this.netexIdHelper = netexIdHelper;
    }

    public List<EntityInVersionStructure> createServiceFrameElements(StopPlace stopPlace) {
        List <EntityInVersionStructure> serviceFrameElements = new ArrayList<>();
        List<ScheduledStopPoint> scheduledStopPoints = new ArrayList<>();
        List<PassengerStopAssignment> passengerStopAssignments = new ArrayList<>();

        createServiceFrameElements(stopPlace, scheduledStopPoints, passengerStopAssignments, 0);

        serviceFrameElements.addAll(scheduledStopPoints);
        serviceFrameElements.addAll(passengerStopAssignments);

        return serviceFrameElements;
    }

    public void createServiceFrameElements(StopPlace stopPlace, List<ScheduledStopPoint> scheduledStopPoints, List<PassengerStopAssignment> passengerStopAssignments, Integer passengerStopAssignmentOrderOverride) {
        final String netexId = stopPlace.getNetexId();
        String stopPlaceName = null;
        if (stopPlace.getName() != null) {
            stopPlaceName = stopPlace.getName().getValue();
        }
        final long version = stopPlace.getVersion();
        var stopPlaceNetexId = netexIdHelper.extractIdPostfix(netexId);
        var idPrefix = netexIdHelper.extractIdPrefix(netexId);
        var scheduledStopPointNetexId = idPrefix + ":ScheduledStopPoint:S" + stopPlaceNetexId;

        LocalDateTime validFrom = null;
        LocalDateTime validTo = null;
        if (stopPlace.getValidBetween() != null) {
            if (stopPlace.getValidBetween().getFromDate() != null) {
                validFrom = LocalDateTime.ofInstant(stopPlace.getValidBetween().getFromDate(), ZoneId.systemDefault());
            }
            if (stopPlace.getValidBetween().getToDate() != null) {
                validTo = LocalDateTime.ofInstant(stopPlace.getValidBetween().getToDate(), ZoneId.systemDefault());
            }
        }

        scheduledStopPoints.add(createNetexScheduledStopPoint(scheduledStopPointNetexId, stopPlaceName, version, validFrom, validTo));
        int passengerStopAssignmentOrder = passengerStopAssignmentOrderOverride != null ? passengerStopAssignmentOrderOverride : passengerStopAssignments.size() + 1;
        passengerStopAssignments.add(createPassengerStopAssignment(netexId, version, scheduledStopPointNetexId, passengerStopAssignmentOrder, validFrom, validTo, false));

        // Add quays
        final Set<Quay> quays = stopPlace.getQuays() != null ? stopPlace.getQuays() : Set.of();
        for (Quay quay : quays) {
            var quayNetexId = netexIdHelper.extractIdPostfix(quay.getNetexId());
            var quayUdPrefix = netexIdHelper.extractIdPrefix(quay.getNetexId());
            var quayScheduledStopPointNetexId = quayUdPrefix + ":ScheduledStopPoint:Q" + quayNetexId;
            scheduledStopPoints.add(createNetexScheduledStopPoint(quayScheduledStopPointNetexId, stopPlaceName, quay.getVersion(), validFrom, validTo));
            int quayPassengerStopAssignmentOrder = passengerStopAssignmentOrderOverride != null ? passengerStopAssignmentOrderOverride : passengerStopAssignments.size() + 1;
            passengerStopAssignments.add(createPassengerStopAssignment(quay.getNetexId(), quay.getVersion(), quayScheduledStopPointNetexId, quayPassengerStopAssignmentOrder, validFrom, validTo, true));
        }
    }

    private ScheduledStopPoint createNetexScheduledStopPoint(String scheduledStopPointNetexId, String stopPlaceName, long version, LocalDateTime validFrom, LocalDateTime validTo) {
        final org.rutebanken.netex.model.ScheduledStopPoint netexScheduledStopPoint = new org.rutebanken.netex.model.ScheduledStopPoint();
        netexScheduledStopPoint.setId(scheduledStopPointNetexId);
        netexScheduledStopPoint.setVersion(String.valueOf(version));
        netexScheduledStopPoint.withName(new MultilingualString().withValue(stopPlaceName));
        ValidBetween validBetween = new ValidBetween().withFromDate(validFrom).withToDate(validTo);

        netexScheduledStopPoint.withValidBetween(validBetween);

        return netexScheduledStopPoint;
    }

    private PassengerStopAssignment createPassengerStopAssignment(String netexId, long version, String scheduledStopPointNetexId, int passengerStopAssignmentOrder, LocalDateTime validFrom, LocalDateTime validTo, boolean isQuay) {
        String passengerStopAssignmentId = netexIdHelper.extractIdPostfix(scheduledStopPointNetexId);
        String idPrefix = netexIdHelper.extractIdPrefix(scheduledStopPointNetexId);
        PassengerStopAssignment passengerStopAssignment = new PassengerStopAssignment();
        passengerStopAssignment.withId(idPrefix + ":PassengerStopAssignment:P" + passengerStopAssignmentId);
        passengerStopAssignment.withVersion(String.valueOf(version));
        passengerStopAssignment.withOrder(BigInteger.valueOf(passengerStopAssignmentOrder));

        ValidBetween validBetween = new ValidBetween().withFromDate(validFrom).withToDate(validTo);
        passengerStopAssignment.withValidBetween(validBetween);
        if (isQuay) {
            passengerStopAssignment.withQuayRef(new QuayRefStructure().withRef(netexId).withVersion(String.valueOf(version)));
        } else {
            passengerStopAssignment.withStopPlaceRef(new StopPlaceRefStructure().withRef(netexId).withVersion(String.valueOf(version)));
        }
        final JAXBElement<ScheduledStopPointRefStructure> scheduledStopPointRef = objectFactory.createScheduledStopPointRef(new ScheduledStopPointRefStructure().withRef(scheduledStopPointNetexId).withVersionRef(String.valueOf(version)));
        passengerStopAssignment.withScheduledStopPointRef(scheduledStopPointRef);

        return passengerStopAssignment;
    }
}
