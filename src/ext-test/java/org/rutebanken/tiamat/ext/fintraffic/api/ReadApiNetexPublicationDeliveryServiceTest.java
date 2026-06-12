package org.rutebanken.tiamat.ext.fintraffic.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityOutRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiSearchKey;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.time.ExportTimeZone;

import java.io.ByteArrayOutputStream;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
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

    @Test
    void streamPublicationDelivery_fareZoneAppearsBeforeStopPlaces() throws Exception {
        String fareZoneXml = "<FareZone id=\"TKL:FareZone:A\" version=\"1\"/>";
        String stopPlaceXml = "<StopPlace id=\"FSR:StopPlace:1\" version=\"1\"/>";
        ReadApiEntityOutRecord fareZoneRecord = new ReadApiEntityOutRecord("FareZone", fareZoneXml);
        ReadApiEntityOutRecord stopPlaceRecord = new ReadApiEntityOutRecord("StopPlace", stopPlaceXml);

        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class)))
                .thenReturn(Stream.of(fareZoneRecord, stopPlaceRecord));

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        service.streamPublicationDelivery(mock(ReadApiSearchKey.class), out);
        String xml = out.toString();

        assertThat(xml.indexOf("<tariffZones>")).isLessThan(xml.indexOf("<stopPlaces>"));
        assertThat(xml).contains("TKL:FareZone:A");
        assertThat(xml).contains("FSR:StopPlace:1");
    }
}
