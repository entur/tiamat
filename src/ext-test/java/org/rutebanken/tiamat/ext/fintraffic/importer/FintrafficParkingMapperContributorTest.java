package org.rutebanken.tiamat.ext.fintraffic.importer;

import ma.glasnost.orika.MappingContext;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

/**
 * Unit tests for {@link FintrafficParkingMapperContributor}.
 */
public class FintrafficParkingMapperContributorTest {

    private FintrafficParkingMapperContributor contributor;
    private MappingContext mappingContext;

    @Before
    public void setUp() {
        contributor = new FintrafficParkingMapperContributor();
        mappingContext = mock(MappingContext.class);
    }

    @Test
    public void mapFromNetex_copiesPaymentMethodsToTransientField() {
        org.rutebanken.netex.model.Parking source = new org.rutebanken.netex.model.Parking()
                .withPaymentMethods(
                        org.rutebanken.netex.model.PaymentMethodEnumeration.CASH,
                        org.rutebanken.netex.model.PaymentMethodEnumeration.CREDIT_CARD);
        Parking target = new Parking();

        contributor.mapFromNetex(source, target, mappingContext);

        assertThat(target.getPaymentMethods())
                .containsExactlyInAnyOrder(
                        PaymentMethodEnumeration.CASH,
                        PaymentMethodEnumeration.CREDIT_CARD);
    }

    @Test
    public void mapFromNetex_emptySource_leavesTargetUnchanged() {
        org.rutebanken.netex.model.Parking source = new org.rutebanken.netex.model.Parking();
        Parking target = new Parking();

        contributor.mapFromNetex(source, target, mappingContext);

        assertThat(target.getPaymentMethods()).isEmpty();
    }

    @Test
    public void mapToNetex_copiesPaymentMethodsFromFintrafficParking() {
        FintrafficParking source = new FintrafficParking();
        source.setPaymentMethods(List.of(
                PaymentMethodEnumeration.CASH,
                PaymentMethodEnumeration.CREDIT_CARD));
        org.rutebanken.netex.model.Parking target = new org.rutebanken.netex.model.Parking();

        contributor.mapToNetex(source, target, mappingContext);

        assertThat(target.getPaymentMethods())
                .containsExactlyInAnyOrder(
                        org.rutebanken.netex.model.PaymentMethodEnumeration.CASH,
                        org.rutebanken.netex.model.PaymentMethodEnumeration.CREDIT_CARD);
    }

    @Test
    public void mapToNetex_plainParking_doesNothing() {
        Parking source = new Parking();
        source.getPaymentMethods().add(PaymentMethodEnumeration.CASH);
        org.rutebanken.netex.model.Parking target = new org.rutebanken.netex.model.Parking();

        contributor.mapToNetex(source, target, mappingContext);

        assertThat(target.getPaymentMethods()).isEmpty();
    }
}
