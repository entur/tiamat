package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.xml.sax.SAXParseException;

import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class PublicationDeliveryStreamingOutputTest {

    /**
     * Publication delivery should not validate.
     * It does not, for instance, contain publication timestamp nor participant ref.
     */
    @Test
    public void cannotStreamInvalidXml() throws Exception {

        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure();

        StreamingOutput streamingOutput = new PublicationDeliveryStreamingOutput().stream(publicationDelivery);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        assertThatThrownBy(() -> {
            streamingOutput.write(byteArrayOutputStream);
        }).hasRootCauseInstanceOf(SAXParseException.class);
    }

    @Test
    public void streamValidXml() throws Exception {
        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                .withPublicationTimestamp(OffsetDateTime.now())
                .withParticipantRef("participantRef");

        StreamingOutput streamingOutput = new PublicationDeliveryStreamingOutput().stream(publicationDelivery);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        streamingOutput.write(byteArrayOutputStream);
    }

}