package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import javax.xml.bind.UnmarshalException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;


public class PublicationDeliveryUnmarshallerTest {

    @Test
    public void expectUnmarshalExceptionWhenIncorrectPublicationDeliveryXml() throws IOException, SAXException, JAXBException {

        String notValidPublicationDeliveryXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<PublicationDelivery xmlns=\"http://www.netex.org.uk/netex\" xmlns:ns2=\"http://www.opengis.net/gml/3.2\" xmlns:ns3=\"http://www.siri.org.uk/siri\">\n" +
                "</PublicationDeliivery>";

        InputStream inputStream = new ByteArrayInputStream(notValidPublicationDeliveryXml.getBytes());

        PublicationDeliveryUnmarshaller publicationDeliveryUnmarshaller = new PublicationDeliveryUnmarshaller();

        assertThatThrownBy(() -> publicationDeliveryUnmarshaller.unmarshal(inputStream))
                .isInstanceOf(UnmarshalException.class);

    }
}