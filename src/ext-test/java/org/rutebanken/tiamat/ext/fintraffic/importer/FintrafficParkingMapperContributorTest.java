package org.rutebanken.tiamat.ext.fintraffic.importer;

import ma.glasnost.orika.MappingContext;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.netex.model.GroupOfEntities_VersionStructure;
import org.rutebanken.netex.model.InfoLinkStructure;
import org.rutebanken.netex.model.TypeOfInfolinkEnumeration;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficInfoLink;
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

    // --- paymentMethods ---

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

    // --- infoLinks ---

    @Test
    public void mapFromNetex_copiesInfoLinksToFintrafficParking() {
        var infoLinks = new GroupOfEntities_VersionStructure.InfoLinks()
                .withInfoLink(
                        new InfoLinkStructure()
                                .withValue("https://example.com/parking")
                                .withTypeOfInfoLink(TypeOfInfolinkEnumeration.RESOURCE),
                        new InfoLinkStructure()
                                .withValue("https://example.com/info"));
        org.rutebanken.netex.model.Parking source = new org.rutebanken.netex.model.Parking();
        source.setInfoLinks(infoLinks);
        FintrafficParking target = new FintrafficParking();

        contributor.mapFromNetex(source, target, mappingContext);

        assertThat(target.getInfoLinks())
                .hasSize(2)
                .contains(new FintrafficInfoLink("https://example.com/parking", "resource"))
                .contains(new FintrafficInfoLink("https://example.com/info", null));
    }

    @Test
    public void mapFromNetex_noInfoLinks_leavesTargetEmpty() {
        org.rutebanken.netex.model.Parking source = new org.rutebanken.netex.model.Parking();
        FintrafficParking target = new FintrafficParking();

        contributor.mapFromNetex(source, target, mappingContext);

        assertThat(target.getInfoLinks()).isEmpty();
    }

    @Test
    public void mapFromNetex_plainParking_infoLinksIgnored() {
        var infoLinks = new GroupOfEntities_VersionStructure.InfoLinks()
                .withInfoLink(new InfoLinkStructure().withValue("https://example.com"));
        org.rutebanken.netex.model.Parking source = new org.rutebanken.netex.model.Parking();
        source.setInfoLinks(infoLinks);
        Parking target = new Parking(); // plain, not FintrafficParking

        contributor.mapFromNetex(source, target, mappingContext);

        // no exception, just silently ignored
    }

    @Test
    public void mapToNetex_copiesInfoLinksFromFintrafficParking() {
        FintrafficParking source = new FintrafficParking();
        source.setInfoLinks(List.of(
                new FintrafficInfoLink("https://example.com/resource", "resource"),
                new FintrafficInfoLink("https://example.com/plain", null)));
        org.rutebanken.netex.model.Parking target = new org.rutebanken.netex.model.Parking();

        contributor.mapToNetex(source, target, mappingContext);

        assertThat(target.getInfoLinks()).isNotNull();
        var netexLinks = target.getInfoLinks().getInfoLink();
        assertThat(netexLinks).hasSize(2);
        assertThat(netexLinks.get(0).getValue()).isEqualTo("https://example.com/resource");
        assertThat(netexLinks.get(0).getTypeOfInfoLink())
                .containsExactly(TypeOfInfolinkEnumeration.RESOURCE);
        assertThat(netexLinks.get(1).getValue()).isEqualTo("https://example.com/plain");
        assertThat(netexLinks.get(1).getTypeOfInfoLink()).isEmpty();
    }

    @Test
    public void mapToNetex_emptyInfoLinks_doesNotSetField() {
        FintrafficParking source = new FintrafficParking();
        org.rutebanken.netex.model.Parking target = new org.rutebanken.netex.model.Parking();

        contributor.mapToNetex(source, target, mappingContext);

        assertThat(target.getInfoLinks()).isNull();
    }
}
