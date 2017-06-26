package org.rutebanken.tiamat.exporter;

import com.sun.xml.xsom.impl.scd.Iterators;
import org.apache.commons.collections.iterators.ArrayListIterator;
import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.Parking;
import org.rutebanken.tiamat.model.StopPlace;
import org.rutebanken.tiamat.model.TopographicPlace;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.repository.ParkingRepository;
import org.rutebanken.tiamat.repository.StopPlaceRepository;
import org.rutebanken.tiamat.repository.TariffZoneRepository;
import org.rutebanken.tiamat.repository.TopographicPlaceRepository;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.xml.bind.JAXBContext.newInstance;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StreamingPublicationDeliveryTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
    private ParkingRepository parkingRepository = mock(ParkingRepository.class);

    private TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
    private TiamatSiteFrameExporter tiamatSiteFrameExporter = new TiamatSiteFrameExporter(topographicPlaceRepository, mock(TariffZoneRepository.class));
    private NetexMapper netexMapper = new NetexMapper();
    private PublicationDeliveryExporter publicationDeliveryExporter = new PublicationDeliveryExporter(stopPlaceRepository, netexMapper, tiamatSiteFrameExporter);
    private PublicationDeliveryHelper publicationDeliveryHelper = new PublicationDeliveryHelper();
    private StreamingPublicationDelivery streamingPublicationDelivery = new StreamingPublicationDelivery(publicationDeliveryHelper, stopPlaceRepository, parkingRepository, publicationDeliveryExporter, tiamatSiteFrameExporter, netexMapper);

    @Test
    public void streamStopPlaceIntoPublicationDelivery() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace.setNetexId(NetexIdHelper.generateRandomizedNetexId(stopPlace));

        List<StopPlace> stopPlaces = new ArrayList<>(2);
        stopPlaces.add(stopPlace);

        stream(stopPlaces, new ArrayList<>(), byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        assertThat(xml)
                .contains("<StopPlace")
                .contains("</PublicationDelivery")
                .contains("</dataObjects>");
    }
    @Test
    public void streamParkingIntoPublicationDelivery() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        Parking parking = new Parking();
        parking.setNetexId(NetexIdHelper.generateRandomizedNetexId(parking));

        List<Parking> parkings = new ArrayList<>(2);
        parkings.add(parking);

        stream(new ArrayList<>(), parkings, byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        assertThat(xml)
                .contains("<Parking")
                .contains("</PublicationDelivery")
                .contains("</dataObjects>");
    }

    @Test
    public void streamStopPlaceIntoPublicationDeliveryWithTopographicPlace() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace.setNetexId(NetexIdHelper.generateRandomizedNetexId(stopPlace));

        List<StopPlace> stopPlaces = new ArrayList<>(2);
        stopPlaces.add(stopPlace);


        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("TP"));
        topographicPlace.setVersion(1);
        topographicPlace.setNetexId("NSR:TopographicPlace:2");
        when(topographicPlaceRepository.findAll()).thenReturn(Arrays.asList(topographicPlace));

        stream(stopPlaces, new ArrayList<>(), byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        assertThat(xml)
                .contains("<StopPlace")
                .contains("<topographicPlaces")
                .contains("</topographicPlaces>")
                .contains("</PublicationDelivery")
                .contains("</dataObjects>");
    }

    @Test
    public void streamStopPlaceAndValidateResult() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace.setNetexId(NetexIdHelper.generateRandomizedNetexId(stopPlace));
        stopPlace.setVersion(2L);

        List<StopPlace> stopPlaces = new ArrayList<>(1);
        stopPlaces.add(stopPlace);

        stream(stopPlaces, new ArrayList<>(), byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();
        System.out.println(xml);

        validate(xml);
    }

    private void validate(String xml) throws JAXBException, IOException, SAXException {
        JAXBContext publicationDeliveryContext = newInstance(PublicationDeliveryStructure.class);
        Unmarshaller unmarshaller = publicationDeliveryContext.createUnmarshaller();

        NeTExValidator neTExValidator = new NeTExValidator();
        unmarshaller.setSchema(neTExValidator.getSchema());
        unmarshaller.unmarshal(new StringReader(xml));
    }

    private void stream(List<StopPlace> stopPlaces, List<Parking> parkings, ByteArrayOutputStream byteArrayOutputStream) throws InterruptedException, IOException, XMLStreamException, JAXBException {
        when(parkingRepository.scrollParkings()).thenReturn(parkings.iterator());
        when(stopPlaceRepository.scrollStopPlaces(any())).thenReturn(stopPlaces.iterator());
        streamingPublicationDelivery.stream(new ExportParams(), byteArrayOutputStream);
    }

}