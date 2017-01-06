package org.rutebanken.tiamat.exporters;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.tiamat.TiamatApplication;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.repository.ExportJobRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.StopPlaceSearch;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryStreamingOutput;
import org.rutebanken.tiamat.service.BlobStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.Iterator;

import static javax.xml.bind.JAXBContext.newInstance;
import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;


//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = TiamatApplication.class)
//@ActiveProfiles("geodb")
public class AsyncPublicationDeliveryExporterTest {


    private NetexMapper netexMapper = new NetexMapper();

//    @Autowired
    private PublicationDeliveryExporter publicationDeliveryExporter = new PublicationDeliveryExporter(mock(StopPlaceRepository.class),
        mock(TopographicPlaceRepository.class), netexMapper);


//    @Autowired
//    private AsyncPublicationDeliveryExporter asyncPublicationDeliveryExporter = new AsyncPublicationDeliveryExporter(publicationDeliveryExporter, mock(ex))




    /**
     * Test uploading export file to google blob store.
     * Ignored. Intended for manual use.
     */
    @Ignore
    @Test
    public void startExportJob() throws Exception {

//        asyncPublicationDeliveryExporter.startExportJob(new StopPlaceSearch());

        Thread.sleep(10000);
    }


    @Test
    public void test() throws JAXBException, ParserConfigurationException, IOException, SAXException, TransformerException, XMLStreamException {


        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportPublicationDeliveryWithoutStops();


        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place"));

        org.rutebanken.netex.model.StopPlace netexStopPlace = netexMapper.mapToNetexModel(stopPlace);







//
//
//        DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
//
//        DocumentBuilder builder = domFactory.newDocumentBuilder();
//        Document doc = builder.parse(new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
//        TransformerFactory transFactory = TransformerFactory.newInstance();
//        Transformer transformer = transFactory.newTransformer();
//
//        StringWriter buffer = new StringWriter();
//        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "false");
//
//
//        for(int i = 0; i < doc.getChildNodes().getLength(); i++) {
//
//            Node node = doc.getChildNodes().item(i);
//            transformer.transform(new DOMSource(node), new StreamResult(buffer));
//
//        }
//        System.out.println(buffer.toString());
//

    }




}