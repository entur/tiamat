package org.rutebanken.tiamat.ext.fintraffic.model;

import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.factory.ParkingEntityFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Fintraffic override of {@link ParkingEntityFactory}.
 * <p>
 * Active when the {@code fintraffic} Spring profile is present. Tells the
 * core to instantiate {@link FintrafficParking} instead of {@link Parking},
 * and removes {@code paymentMethods} from the Orika exclusion list so the
 * field is mapped during NeTEx import and export.
 */
@Primary
@Component
@Profile("fintraffic")
public class FintrafficParkingEntityFactory extends ParkingEntityFactory {

    @Override
    public Parking create() {
        return new FintrafficParking();
    }

    @Override
    public Class<? extends Parking> getEntityClass() {
        return FintrafficParking.class;
    }

    @Override
    public List<String> getMappingExclusions() {
        // paymentMethods intentionally absent — it is persisted by FintrafficParking
        return List.of("cardsAccepted", "currenciesAccepted", "accessModes");
    }
}
