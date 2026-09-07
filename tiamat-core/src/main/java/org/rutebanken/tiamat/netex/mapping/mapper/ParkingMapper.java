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
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.Parking;
import org.rutebanken.netex.model.ParkingArea;
import org.rutebanken.netex.model.ParkingAreas_RelStructure;
import org.rutebanken.netex.model.ParkingEntranceForVehicles;
import org.rutebanken.netex.model.ParkingEntrancesForVehicles_RelStructure;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;

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
            ParkingEntrancesForVehicles_RelStructure rel = new ParkingEntrancesForVehicles_RelStructure();
            rel.getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles().addAll(entrances);
            target.setVehicleEntrances(rel);
        }
    }
}
