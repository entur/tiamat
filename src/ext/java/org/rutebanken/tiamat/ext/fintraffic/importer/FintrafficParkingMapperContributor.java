package org.rutebanken.tiamat.ext.fintraffic.importer;

import ma.glasnost.orika.MappingContext;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;
import org.rutebanken.tiamat.netex.mapping.mapper.ParkingMapperContributor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Fintraffic extension of {@link ParkingMapperContributor} that wires
 * {@code paymentMethods} through the NeTEx ↔ Tiamat mapping in both directions.
 *
 * <p><b>Import ({@link #mapFromNetex}):</b> copies the NeTEx
 * {@code paymentMethods} list onto the Tiamat {@link org.rutebanken.tiamat.model.Parking}
 * {@code @Transient} field so that the
 * {@link FintrafficMergingParkingImporter#mergeExtendedFields} hook can persist it.
 *
 * <p><b>Export ({@link #mapToNetex}):</b> reads {@code paymentMethods} from a
 * {@link FintrafficParking} and writes it back into the NeTEx output so that the
 * field survives the export roundtrip.
 */
@Profile("fintraffic")
@Component
public class FintrafficParkingMapperContributor implements ParkingMapperContributor {

    @Override
    public void mapFromNetex(org.rutebanken.netex.model.Parking source,
                             org.rutebanken.tiamat.model.Parking target,
                             MappingContext context) {
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

    @Override
    public void mapToNetex(org.rutebanken.tiamat.model.Parking source,
                           org.rutebanken.netex.model.Parking target,
                           MappingContext context) {
        if (!(source instanceof FintrafficParking fp)) {
            return;
        }
        var methods = fp.getPaymentMethods();
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
}
