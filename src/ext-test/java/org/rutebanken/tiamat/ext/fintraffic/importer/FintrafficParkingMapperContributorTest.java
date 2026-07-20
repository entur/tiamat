package org.rutebanken.tiamat.ext.fintraffic.importer;

import ma.glasnost.orika.MappingContext;
import org.junit.Before;
import org.junit.Test;
import org.rutebanken.netex.model.EntranceEnumeration;
import org.rutebanken.netex.model.GroupOfEntities_VersionStructure;
import org.rutebanken.netex.model.InfoLinkStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ParkingEntranceForVehicles;
import org.rutebanken.netex.model.ParkingEntrancesForVehicles_RelStructure;
import org.rutebanken.netex.model.TypeOfInfolinkEnumeration;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficInfoLink;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParking;
import org.rutebanken.tiamat.ext.fintraffic.model.FintrafficParkingEntranceForVehicles;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.PaymentMethodEnumeration;

import java.math.BigDecimal;
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

    // --- vehicleEntrances ---

    @Test
    public void mapFromNetex_copiesVehicleEntrancesToFintrafficParking() {
        ParkingEntranceForVehicles entrance = new ParkingEntranceForVehicles()
                .withLabel(new MultilingualString().withValue("Main entrance"))
                .withEntranceType(EntranceEnumeration.DOOR)
                .withWidth(new BigDecimal("3.50"))
                .withHeight(new BigDecimal("2.20"))
                .withIsEntry(true)
                .withIsExit(false)
                .withPublicCode("A1");
        ParkingEntrancesForVehicles_RelStructure relStruct = new ParkingEntrancesForVehicles_RelStructure()
                .withParkingEntranceForVehiclesRefOrParkingEntranceForVehicles(entrance);
        org.rutebanken.netex.model.Parking source = new org.rutebanken.netex.model.Parking();
        source.setVehicleEntrances(relStruct);
        FintrafficParking target = new FintrafficParking();

        contributor.mapFromNetex(source, target, mappingContext);

        assertThat(target.getFintrafficVehicleEntrances()).hasSize(1);
        FintrafficParkingEntranceForVehicles mapped = target.getFintrafficVehicleEntrances().getFirst();
        assertThat(mapped.getLabel()).isEqualTo("Main entrance");
        assertThat(mapped.getEntranceType()).isEqualTo("door");
        assertThat(mapped.getWidth()).isEqualByComparingTo(new BigDecimal("3.50"));
        assertThat(mapped.getHeight()).isEqualByComparingTo(new BigDecimal("2.20"));
        assertThat(mapped.getIsEntry()).isTrue();
        assertThat(mapped.getIsExit()).isFalse();
        assertThat(mapped.getPublicCode()).isEqualTo("A1");
    }

    @Test
    public void mapFromNetex_noVehicleEntrances_leavesTargetEmpty() {
        org.rutebanken.netex.model.Parking source = new org.rutebanken.netex.model.Parking();
        FintrafficParking target = new FintrafficParking();

        contributor.mapFromNetex(source, target, mappingContext);

        assertThat(target.getFintrafficVehicleEntrances()).isEmpty();
    }

    @Test
    public void mapToNetex_copiesVehicleEntrancesFromFintrafficParking() {
        FintrafficParking source = new FintrafficParking();
        source.setFintrafficVehicleEntrances(List.of(
                new FintrafficParkingEntranceForVehicles("Exit", "gate",
                        new BigDecimal("4.00"), new BigDecimal("3.00"), false, true, "B2")));
        org.rutebanken.netex.model.Parking target = new org.rutebanken.netex.model.Parking();

        contributor.mapToNetex(source, target, mappingContext);

        assertThat(target.getVehicleEntrances()).isNotNull();
        var items = target.getVehicleEntrances()
                .getParkingEntranceForVehiclesRefOrParkingEntranceForVehicles();
        assertThat(items).hasSize(1);
        ParkingEntranceForVehicles netex = (ParkingEntranceForVehicles) items.getFirst();
        assertThat(netex.getLabel().getValue()).isEqualTo("Exit");
        assertThat(netex.getEntranceType()).isEqualTo(EntranceEnumeration.GATE);
        assertThat(netex.getWidth()).isEqualByComparingTo(new BigDecimal("4.00"));
        assertThat(netex.getHeight()).isEqualByComparingTo(new BigDecimal("3.00"));
        assertThat(netex.isIsEntry()).isFalse();
        assertThat(netex.isIsExit()).isTrue();
        assertThat(netex.getPublicCode()).isEqualTo("B2");
    }

    @Test
    public void mapToNetex_emptyVehicleEntrances_doesNotSetField() {
        FintrafficParking source = new FintrafficParking();
        org.rutebanken.netex.model.Parking target = new org.rutebanken.netex.model.Parking();

        contributor.mapToNetex(source, target, mappingContext);

        assertThat(target.getVehicleEntrances()).isNull();
    }
}
