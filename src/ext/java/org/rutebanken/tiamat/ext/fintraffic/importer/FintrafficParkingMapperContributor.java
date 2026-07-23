package org.rutebanken.tiamat.ext.fintraffic.importer;

import ma.glasnost.orika.MappingContext;
import org.rutebanken.netex.model.EntranceEnumeration;
import org.rutebanken.netex.model.InfoLinkStructure;
import org.rutebanken.netex.model.ParkingEntranceForVehicles;
import org.rutebanken.netex.model.ParkingEntrancesForVehicles_RelStructure;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficInfoLink;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingEntranceForVehicles;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.ParkingMapperContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
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

    @Override
    public void mapFromNetex(org.rutebanken.netex.model.Parking source,
                             org.rutebanken.tiamat.model.Parking target,
                             MappingContext context) {
        mapPaymentMethodsFromNetex(source, target);
        mapInfoLinksFromNetex(source, target);
        mapVehicleEntrancesFromNetex(source, target);
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
}

