package org.rutebanken.tiamat.ext.fintraffic.model;

import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class FintrafficParkingEntityFactoryTest {

    private final FintrafficParkingEntityFactory factory = new FintrafficParkingEntityFactory();

    @Test
    void create_returnsFintrafficParkingInstance() {
        Parking parking = factory.create();

        assertThat(parking).isInstanceOf(FintrafficParking.class);
    }

    @Test
    void getEntityClass_returnsFintrafficParkingClass() {
        assertThat(factory.getEntityClass()).isEqualTo(FintrafficParking.class);
    }

    @Test
    void getMappingExclusions_containsPaymentMethods() {
        List<String> exclusions = factory.getMappingExclusions();

        assertThat(exclusions)
                .as("paymentMethods must be excluded from Orika auto-mapping: the two PaymentMethodEnumeration types differ and require explicit conversion in FintrafficParkingMapperContributor")
                .contains("paymentMethods");
    }

    @Test
    void getMappingExclusions_stillExcludesOtherUnsupportedFields() {
        List<String> exclusions = factory.getMappingExclusions();

        assertThat(exclusions).contains("cardsAccepted", "currenciesAccepted", "accessModes");
    }
}
