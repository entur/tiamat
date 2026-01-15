package org.rutebanken.tiamat.ext.fintraffic.api;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiEntityOutRecord;
import org.rutebanken.tiamat.ext.fintraffic.api.model.ReadApiSearchKey;
import org.rutebanken.tiamat.ext.fintraffic.api.repository.NetexRepository;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.rutebanken.tiamat.time.ExportTimeZone;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.ZoneId;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class FintrafficApiControllerTest {

    private NetexRepository netexRepository;
    private FintrafficApiController controller;
    private HttpServletResponse response;
    private ByteArrayOutputStream outputStream;

    @BeforeEach
    void setUp() throws IOException {
        netexRepository = mock(NetexRepository.class);

        // Create real service with mocked dependencies
        ValidPrefixList validPrefixList = mock(ValidPrefixList.class);
        when(validPrefixList.getValidNetexPrefix()).thenReturn("FSR");

        ExportTimeZone exportTimeZone = mock(ExportTimeZone.class);
        when(exportTimeZone.getDefaultTimeZoneId()).thenReturn(ZoneId.of("Europe/Helsinki"));

        ReadApiNetexPublicationDeliveryService publicationDeliveryService = new ReadApiNetexPublicationDeliveryService(
                netexRepository,
                validPrefixList,
                exportTimeZone,
                "fin",
                "1.12:FI-NeTEx-stops:1.0"
        );

        controller = new FintrafficApiController(publicationDeliveryService);

        // Setup mock response
        response = mock(HttpServletResponse.class);
        outputStream = new ByteArrayOutputStream();
        ServletOutputStream servletOutputStream = new ServletOutputStream() {
            @Override
            public void write(int b) {
                outputStream.write(b);
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setWriteListener(jakarta.servlet.WriteListener writeListener) {
            }
        };
        when(response.getOutputStream()).thenReturn(servletOutputStream);
    }

    @Test
    void getNetexStreamWithMultipleEntityTypes() {
        String[] transportModes = {"bus"};
        String[] areaCodes = {"ABC"};

        ReadApiEntityOutRecord scheduledStopPoint = new ReadApiEntityOutRecord(
                "ScheduledStopPoint",
                "<ScheduledStopPoint id=\"FSR:ScheduledStopPoint:1\" version=\"1\"/>".getBytes(StandardCharsets.UTF_8)
        );

        ReadApiEntityOutRecord stopPlace = new ReadApiEntityOutRecord(
                "StopPlace",
                "<StopPlace id=\"FSR:StopPlace:1\" version=\"1\"/>".getBytes(StandardCharsets.UTF_8)
        );

        ReadApiEntityOutRecord parking = new ReadApiEntityOutRecord(
                "Parking",
                "<Parking id=\"FSR:Parking:1\" version=\"1\"/>".getBytes(StandardCharsets.UTF_8)
        );

        when(netexRepository.streamStopPlaces(any(ReadApiSearchKey.class)))
                .thenReturn(Stream.of(scheduledStopPoint, stopPlace, parking));

        controller.getNetexStream(transportModes, areaCodes, response);

        String output = outputStream.toString(StandardCharsets.UTF_8);
        assertThat(output, containsString("FSR:ScheduledStopPoint:1"));
        assertThat(output, containsString("FSR:StopPlace:1"));
        assertThat(output, containsString("FSR:Parking:1"));
        assertThat(output, containsString("<scheduledStopPoints>"));
        assertThat(output, containsString("</scheduledStopPoints>"));
        assertThat(output, containsString("<stopPlaces>"));
        assertThat(output, containsString("</stopPlaces>"));
        assertThat(output, containsString("<parkings>"));
        assertThat(output, containsString("</parkings>"));
    }
}

