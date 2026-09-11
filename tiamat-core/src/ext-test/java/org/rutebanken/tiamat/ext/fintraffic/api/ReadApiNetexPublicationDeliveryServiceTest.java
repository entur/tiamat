package org.rutebanken.tiamat.ext.fintraffic.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityOutRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiSearchKey;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.time.ExportTimeZone;

import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReadApiNetexPublicationDeliveryServiceTest {

    private NetexRepository netexRepository;
    private ReadApiNetexPublicationDeliveryService service;

    @BeforeEach
    void setUp() {
        netexRepository = mock(NetexRepository.class);

        ValidPrefixList prefixList = mock(ValidPrefixList.class);
        when(prefixList.getValidNetexPrefix()).thenReturn("FSR");

        ExportTimeZone exportTimeZone = mock(ExportTimeZone.class);
        when(exportTimeZone.getDefaultTimeZoneId()).thenReturn(ZoneId.of("Europe/Helsinki"));

        service = new ReadApiNetexPublicationDeliveryService(
                netexRepository,
                prefixList,
                exportTimeZone,
                "fin",
                "1.12:NO-NeTEx-stops:1.4"
        );
    }

    @Test
    void streamPublicationDelivery_withNoEntities_producesValidPublicationDelivery() throws Exception {
        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class))).thenReturn(Stream.of());

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.streamPublicationDelivery(mock(ReadApiSearchKey.class), out);
        String xml = out.toString();

        assertThat(xml).contains("<PublicationDelivery");
        assertThat(xml).contains("<ServiceFrame");
        assertThat(xml).contains("<SiteFrame");
        // Empty collection placeholders are suppressed — no collection open/close tags expected
        assertThat(xml).doesNotContain("<tariffZones>");
        assertThat(xml).doesNotContain("<stopPlaces>");
    }

    @Test
    void streamPublicationDelivery_withFareZone_includesTariffZonesCollection() throws Exception {
        String fareZoneXml = "<FareZone id=\"TKL:FareZone:A\" version=\"1\"><Name>Zone A</Name></FareZone>";
        ReadApiEntityOutRecord fareZoneRecord = new ReadApiEntityOutRecord("FareZone", fareZoneXml);

        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class)))
                .thenReturn(Stream.of(fareZoneRecord));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.streamPublicationDelivery(mock(ReadApiSearchKey.class), out);
        String xml = out.toString();

        assertThat(xml).contains("<tariffZones>");
        assertThat(xml).contains("TKL:FareZone:A");
        assertThat(xml).contains("</tariffZones>");
    }

    /**
     * Validates the given bytes against the real NeTEx XSD (bundled in netex-java-model), the same
     * schema Tiamat's native import/export paths enforce via JAXB. The Read API assembles XML by
     * hand and bypasses that safeguard, which is how DPO-4978 (invalid SiteFrame element order) went
     * unnoticed - this assertion closes that gap.
     */
    private static void assertValidatesAgainstNetexSchema(byte[] xml) throws Exception {
        assertThatCode(() -> NeTExValidator.getNeTExValidator(NeTExValidator.NetexVersion.v1_15)
                .validate(new StreamSource(new ByteArrayInputStream(xml))))
                .doesNotThrowAnyException();
    }

    @Test
    void streamPublicationDelivery_withAllEntityTypes_validatesAgainstNetexSchema() throws Exception {
        String scheduledStopPointXml = "<ScheduledStopPoint version=\"1\" id=\"FSR:ScheduledStopPoint:1\">"
                + "<Name>Test stop</Name></ScheduledStopPoint>";
        String passengerStopAssignmentXml = "<PassengerStopAssignment order=\"0\" version=\"1\" id=\"FSR:PassengerStopAssignment:1\">"
                + "<ScheduledStopPointRef ref=\"FSR:ScheduledStopPoint:1\" versionRef=\"1\"/>"
                + "<QuayRef ref=\"FSR:Quay:1\" version=\"1\"/></PassengerStopAssignment>";
        String topographicPlaceXml = "<TopographicPlace version=\"1\" id=\"FSR:TopographicPlace:1\">"
                + "<Descriptor><Name lang=\"fi\">Testikunta</Name></Descriptor>"
                + "<TopographicPlaceType>municipality</TopographicPlaceType></TopographicPlace>";
        String stopPlaceXml = "<StopPlace version=\"1\" id=\"FSR:StopPlace:1\">"
                + "<Name lang=\"fi\">Testipysäkki</Name><TransportMode>bus</TransportMode>"
                + "<quays><Quay version=\"1\" id=\"FSR:Quay:1\"/></quays></StopPlace>";
        String parkingXml = "<Parking version=\"1\" id=\"FSR:Parking:1\">"
                + "<Name>Testparkki</Name><ParkingVehicleTypes>car</ParkingVehicleTypes>"
                + "<ParkingLayout>openSpace</ParkingLayout></Parking>";
        String fareZoneXml = "<FareZone version=\"1\" id=\"NYS:FareZone:A\"><Name>Zone A</Name></FareZone>";

        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class))).thenReturn(Stream.of(
                new ReadApiEntityOutRecord("ScheduledStopPoint", scheduledStopPointXml),
                new ReadApiEntityOutRecord("PassengerStopAssignment", passengerStopAssignmentXml),
                new ReadApiEntityOutRecord("TopographicPlace", topographicPlaceXml),
                new ReadApiEntityOutRecord("StopPlace", stopPlaceXml),
                new ReadApiEntityOutRecord("Parking", parkingXml),
                new ReadApiEntityOutRecord("FareZone", fareZoneXml)
        ));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.streamPublicationDelivery(mock(ReadApiSearchKey.class), out);

        assertValidatesAgainstNetexSchema(out.toByteArray());
    }

    @Test
    void streamPublicationDelivery_withOnlyFareZones_validatesAgainstNetexSchema() throws Exception {
        String fareZoneXml = "<FareZone version=\"1\" id=\"NYS:FareZone:A\"><Name>Zone A</Name></FareZone>";

        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class)))
                .thenReturn(Stream.of(new ReadApiEntityOutRecord("FareZone", fareZoneXml)));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.streamPublicationDelivery(mock(ReadApiSearchKey.class), out);

        assertValidatesAgainstNetexSchema(out.toByteArray());
    }

    @Test
    void streamPublicationDelivery_fareZoneAppearsAfterStopPlacesAndParkings() throws Exception {
        String stopPlaceXml = "<StopPlace id=\"FSR:StopPlace:1\" version=\"1\"/>";
        String parkingXml = "<Parking id=\"FSR:Parking:1\" version=\"1\"/>";
        String fareZoneXml = "<FareZone id=\"TKL:FareZone:A\" version=\"1\"/>";
        ReadApiEntityOutRecord stopPlaceRecord = new ReadApiEntityOutRecord("StopPlace", stopPlaceXml);
        ReadApiEntityOutRecord parkingRecord = new ReadApiEntityOutRecord("Parking", parkingXml);
        ReadApiEntityOutRecord fareZoneRecord = new ReadApiEntityOutRecord("FareZone", fareZoneXml);

        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class)))
                .thenReturn(Stream.of(stopPlaceRecord, parkingRecord, fareZoneRecord));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.streamPublicationDelivery(mock(ReadApiSearchKey.class), out);
        String xml = out.toString();

        // NeTEx SiteFrameGroup requires TariffZoneInFrameGroup after SiteInFrameGroup
        assertThat(xml.indexOf("<stopPlaces>")).isLessThan(xml.indexOf("<parkings>"));
        assertThat(xml.indexOf("<parkings>")).isLessThan(xml.indexOf("<tariffZones>"));
        assertThat(xml).contains("TKL:FareZone:A");
        assertThat(xml).contains("FSR:StopPlace:1");
        assertThat(xml).contains("FSR:Parking:1");
    }
}
