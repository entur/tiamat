package org.rutebanken.tiamat.rest.write;

import jakarta.ws.rs.core.StreamingOutput;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;
import org.rutebanken.netex.model.StopPlace;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StopPlaceXmlWriterTest {

    private final StopPlaceXmlWriter stopPlaceXmlWriter =
        new StopPlaceXmlWriter();

    @Test
    void write_shouldProduceValidXml() throws IOException {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId("NSR:StopPlace:42");

        StreamingOutput result = stopPlaceXmlWriter.write(stopPlace);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        result.write(outputStream);

        String xml = outputStream.toString();
        assertNotNull(xml);
        assertTrue(
            xml.contains("StopPlace"),
            "Output XML should contain StopPlace element"
        );
        assertTrue(
            xml.contains("NSR:StopPlace:42"),
            "Output XML should contain the stop place ID"
        );
    }

    @Test
    void write_shouldProduceFormattedXml() throws IOException {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId("NSR:StopPlace:1");

        StreamingOutput result = stopPlaceXmlWriter.write(stopPlace);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        result.write(outputStream);

        String xml = outputStream.toString();
        assertTrue(
            xml.contains("\n"),
            "Formatted XML output should contain newlines"
        );
    }

    @Test
    void write_shouldThrowRuntimeExceptionWhenOutputStreamFails() {
        StopPlace stopPlace = new StopPlace();
        stopPlace.setId("NSR:StopPlace:1");

        StreamingOutput result = stopPlaceXmlWriter.write(stopPlace);

        assertThrows(RuntimeException.class, () ->
            result.write(new BrokenOutputStream())
        );
    }

    private static class BrokenOutputStream extends java.io.OutputStream {

        @Override
        public void write(int b) throws IOException {
            throw new IOException("Simulated write failure");
        }

        @Override
        public void write(byte @NotNull [] b, int off, int len) throws IOException {
            throw new IOException("Simulated write failure");
        }
    }
}
