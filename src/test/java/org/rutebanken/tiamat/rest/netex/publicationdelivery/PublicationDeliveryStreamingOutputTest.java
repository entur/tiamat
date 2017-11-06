/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.xml.sax.SAXParseException;

import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
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
                .withPublicationTimestamp(LocalDateTime.now())
                .withParticipantRef("participantRef");

        StreamingOutput streamingOutput = new PublicationDeliveryStreamingOutput().stream(publicationDelivery);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        streamingOutput.write(byteArrayOutputStream);
    }

}