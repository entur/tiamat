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
import org.rutebanken.netex.model.InfoLinkStructure;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingArea;
import org.rutebanken.netex.model.ParkingAreas_RelStructure;
import org.rutebanken.netex.model.ParkingEntranceForVehicles;
import org.rutebanken.netex.model.ParkingEntrancesForVehicles_RelStructure;
import org.rutebanken.tiamat.model.InfoLink;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.model.TypeOfInfolinkEnumeration;

import java.util.ArrayList;
import java.util.List;

public class ParkingMapper extends CustomMapper<Parking, org.rutebanken.tiamat.model.Parking> {

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
                // skip unknown values
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
                // skip unknown values
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
}
