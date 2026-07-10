package org.rutebanken.tiamat.rest.write.controllers;

import org.rutebanken.tiamat.jersey.interceptor.XmlWhitelist;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

/**
 * StopPlace endpoint currently only supports a subset of the possible xml elements of the nordic netex-profile.
 * Element not listed here will cause a rejection.
 */
@Component
public class SupportedStopPlaceElement implements XmlWhitelist.XmlWhitelistProvider {

    private static final String STOP_PLACE = "stopPlaces/StopPlace";
    private static final String QUAY = STOP_PLACE + "/quays/Quay";
    private static final String BOARDING_POSITION = QUAY + "/boardingPositions/BoardingPosition";

    private static final Set<String> ALLOWED_PATHS = buildAllowedPaths();

    @Override
    public Set<String> allowedPaths() {
        return ALLOWED_PATHS;
    }

    private static Set<String> buildAllowedPaths() {
        Set<String> paths = new HashSet<>();

        addSiteElementPaths(paths, STOP_PLACE);
        addSiteElementPaths(paths, QUAY);

        // StopPlace-specific
        paths.add(STOP_PLACE + "/StopPlaceType");
        paths.add(STOP_PLACE + "/TransportMode");
        paths.add(STOP_PLACE + "/BusSubmode");
        paths.add(STOP_PLACE + "/TramSubmode");
        paths.add(STOP_PLACE + "/RailSubmode");
        paths.add(STOP_PLACE + "/MetroSubmode");
        paths.add(STOP_PLACE + "/AirSubmode");
        paths.add(STOP_PLACE + "/WaterSubmode");
        paths.add(STOP_PLACE + "/TelecabinSubmode");
        paths.add(STOP_PLACE + "/FunicularSubmode");
        paths.add(STOP_PLACE + "/Weighting");
        paths.add(STOP_PLACE + "/Url");
//        paths.add(STOP_PLACE + "/ParentSiteRef"); Will be added when multi-modal is supported
        paths.add(STOP_PLACE + "/ValidBetween/FromDate");
        paths.add(STOP_PLACE + "/ValidBetween/ToDate");
        paths.add(STOP_PLACE + "/tariffZones/TariffZoneRef");
        paths.add(STOP_PLACE + "/adjacentSites/SiteRef");
        paths.add(STOP_PLACE + "/PostalAddress/AddressLine1");
        paths.add(STOP_PLACE + "/PostalAddress/Town");
        paths.add(STOP_PLACE + "/PostalAddress/PostCode");

        // Quay-specific
        paths.add(QUAY + "/Lighting");
        paths.add(QUAY + "/CompassBearing");
        paths.add(BOARDING_POSITION + "/PublicCode");
        paths.add(BOARDING_POSITION + "/Centroid/Location/Longitude");
        paths.add(BOARDING_POSITION + "/Centroid/Location/Latitude");

        return Set.copyOf(paths);
    }

    private static void addSiteElementPaths(Set<String> paths, String prefix) {
        paths.add(prefix + "/Name");
        paths.add(prefix + "/ShortName");
        paths.add(prefix + "/Description");
        paths.add(prefix + "/PublicCode");
        paths.add(prefix + "/PrivateCode");
        paths.add(prefix + "/Centroid/Location/Longitude");
        paths.add(prefix + "/Centroid/Location/Latitude");
        paths.add(prefix + "/keyList/KeyValue/Key");
        paths.add(prefix + "/keyList/KeyValue/Value");
        paths.add(prefix + "/alternativeNames/AlternativeName/NameType");
        paths.add(prefix + "/alternativeNames/AlternativeName/Name");

        // Accessibility assessment
        paths.add(prefix + "/AccessibilityAssessment/MobilityImpairedAccess");
        paths.add(prefix + "/AccessibilityAssessment/limitations/AccessibilityLimitation/WheelchairAccess");
        paths.add(prefix + "/AccessibilityAssessment/limitations/AccessibilityLimitation/StepFreeAccess");
        paths.add(prefix + "/AccessibilityAssessment/limitations/AccessibilityLimitation/EscalatorFreeAccess");
        paths.add(prefix + "/AccessibilityAssessment/limitations/AccessibilityLimitation/LiftFreeAccess");
        paths.add(prefix + "/AccessibilityAssessment/limitations/AccessibilityLimitation/AudibleSignalsAvailable");
        paths.add(prefix + "/AccessibilityAssessment/limitations/AccessibilityLimitation/VisualSignsAvailable");

        // Place equipments
        paths.add(prefix + "/placeEquipments/ShelterEquipment/Enclosed");
        paths.add(prefix + "/placeEquipments/ShelterEquipment/Seats");
        paths.add(prefix + "/placeEquipments/ShelterEquipment/StepFree");
        paths.add(prefix + "/placeEquipments/WaitingRoomEquipment/Seats");
        paths.add(prefix + "/placeEquipments/WaitingRoomEquipment/Heated");
        paths.add(prefix + "/placeEquipments/WaitingRoomEquipment/StepFree");
        paths.add(prefix + "/placeEquipments/SanitaryEquipment/NumberOfToilets");
        paths.add(prefix + "/placeEquipments/SanitaryEquipment/Gender");
        paths.add(prefix + "/placeEquipments/SanitaryEquipment/SanitaryFacilityList");
        paths.add(prefix + "/placeEquipments/TicketingEquipment/TicketOffice");
        paths.add(prefix + "/placeEquipments/TicketingEquipment/TicketMachines");
        paths.add(prefix + "/placeEquipments/TicketingEquipment/NumberOfMachines");
        paths.add(prefix + "/placeEquipments/TicketingEquipment/AudioInterfaceAvailable");
        paths.add(prefix + "/placeEquipments/TicketingEquipment/TactileInterfaceAvailable");
        paths.add(prefix + "/placeEquipments/TicketingEquipment/TicketCounter");
        paths.add(prefix + "/placeEquipments/TicketingEquipment/InductionLoops");
        paths.add(prefix + "/placeEquipments/TicketingEquipment/LowCounterAccess");
        paths.add(prefix + "/placeEquipments/TicketingEquipment/WheelchairSuitable");
        paths.add(prefix + "/placeEquipments/CycleStorageEquipment/NumberOfSpaces");
        paths.add(prefix + "/placeEquipments/CycleStorageEquipment/CycleStorageType");
        paths.add(prefix + "/placeEquipments/GeneralSign/PrivateCode");
        paths.add(prefix + "/placeEquipments/GeneralSign/Content");
        paths.add(prefix + "/placeEquipments/GeneralSign/SignContentType");

        // Local services
        paths.add(prefix + "/localServices/AssistanceService/AssistanceFacilityList");
        paths.add(prefix + "/localServices/AssistanceService/AssistanceAvailability");

        // Facilities (SiteFacilitySet)
        paths.add(prefix + "/facilities/SiteFacilitySet/MobilityFacilityList");
        paths.add(prefix + "/facilities/SiteFacilitySet/PassengerInformationFacilityList");
        paths.add(prefix + "/facilities/SiteFacilitySet/PassengerInformationEquipmentList");
    }
}
