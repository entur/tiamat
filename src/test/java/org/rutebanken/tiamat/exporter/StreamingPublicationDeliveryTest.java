package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.validation.NeTExValidator;
import org.rutebanken.tiamat.exporter.params.ExportParams;
import org.rutebanken.tiamat.model.*;
import org.rutebanken.tiamat.model.identification.IdentifiedEntity;
import org.rutebanken.tiamat.netex.id.NetexIdHelper;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.repository.*;
import org.rutebanken.tiamat.service.stopplace.ParentStopPlacesFetcher;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toSet;
import static javax.xml.bind.JAXBContext.newInstance;
import static org.assertj.core.api.Assertions.anyOf;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anySetOf;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StreamingPublicationDeliveryTest {

    private StopPlaceRepository stopPlaceRepository = mock(StopPlaceRepository.class);
    private ParkingRepository parkingRepository = mock(ParkingRepository.class);

    private PathLinkRepository pathLinkRepository = mock(PathLinkRepository.class);
    private TopographicPlaceRepository topographicPlaceRepository = mock(TopographicPlaceRepository.class);
    private TiamatSiteFrameExporter tiamatSiteFrameExporter = new TiamatSiteFrameExporter(topographicPlaceRepository, mock(TariffZoneRepository.class), pathLinkRepository);

    private TagRepository tagRepository = mock(TagRepository.class);
    private NetexMapper netexMapper = new NetexMapper(tagRepository);

    private TopographicPlacesExporter topographicPlacesExporter = new TopographicPlacesExporter(topographicPlaceRepository, netexMapper);
    private TariffZonesFromStopsExporter tariffZonesFromStopsExporter = mock(TariffZonesFromStopsExporter.class);
    private ParentStopPlacesFetcher parentStopPlacesFetcher = new ParentStopPlacesFetcher(stopPlaceRepository);
    private PublicationDeliveryExporter publicationDeliveryExporter = new PublicationDeliveryExporter(stopPlaceRepository, netexMapper, tiamatSiteFrameExporter, topographicPlacesExporter, tariffZonesFromStopsExporter, parentStopPlacesFetcher);
    private PublicationDeliveryHelper publicationDeliveryHelper = new PublicationDeliveryHelper();
    private TariffZoneRepository tariffZoneRepository = mock(TariffZoneRepository.class);
    private StreamingPublicationDelivery streamingPublicationDelivery = new StreamingPublicationDelivery(publicationDeliveryHelper, stopPlaceRepository,
            parkingRepository, publicationDeliveryExporter, tiamatSiteFrameExporter, topographicPlacesExporter, netexMapper, tariffZoneRepository, topographicPlaceRepository, pathLinkRepository);

    @Test
    public void streamStopPlaceIntoPublicationDelivery() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace.setNetexId(NetexIdHelper.generateRandomizedNetexId(stopPlace));
        setField(IdentifiedEntity.class, "id", stopPlace, 1L);

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
        setField(IdentifiedEntity.class, "id", stopPlace, 2L);
        List<StopPlace> stopPlaces = new ArrayList<>(2);
        stopPlaces.add(stopPlace);


        TopographicPlace topographicPlace = new TopographicPlace(new EmbeddableMultilingualString("TP"));
        topographicPlace.setVersion(1);
        topographicPlace.setNetexId("NSR:TopographicPlace:2");
        when(topographicPlaceRepository.findAll()).thenReturn(Arrays.asList(topographicPlace));
        when(topographicPlaceRepository.getTopographicPlacesFromStopPlaceIds(anySetOf(Long.class))).thenReturn(Arrays.asList(topographicPlace));

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
    public void streamPathLinksIntoPublicationDelivery() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace.setNetexId(NetexIdHelper.generateRandomizedNetexId(stopPlace));
        setField(IdentifiedEntity.class, "id", stopPlace, 1L);

        StopPlace stopPlace2 = new StopPlace(new EmbeddableMultilingualString("stop place 2 in publication delivery"));
        stopPlace2.setNetexId(NetexIdHelper.generateRandomizedNetexId(stopPlace2));
        setField(IdentifiedEntity.class, "id", stopPlace, 2L);

        PathLink pathLink = new PathLink(new PathLinkEnd(new AddressablePlaceRefStructure(stopPlace)), new PathLinkEnd(new AddressablePlaceRefStructure(stopPlace2)));
        setField(IdentifiedEntity.class, "id", pathLink, 1L);

        List<StopPlace> stopPlaces = new ArrayList<>(2);
        stopPlaces.add(stopPlace);
        stopPlaces.add(stopPlace2);

        stream(stopPlaces, new ArrayList<>(), Arrays.asList(pathLink),byteArrayOutputStream);

        String xml = byteArrayOutputStream.toString();

        assertThat(xml)
                .contains("<StopPlace")
                .contains("<PathLink")
                .contains("</PublicationDelivery")
                .contains("</dataObjects>");
    }

    @Test
    public void streamStopPlaceAndValidateResult() throws Exception {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        StopPlace stopPlace = new StopPlace(new EmbeddableMultilingualString("stop place in publication delivery"));
        stopPlace.setNetexId(NetexIdHelper.generateRandomizedNetexId(stopPlace));
        stopPlace.setVersion(2L);
        setField(IdentifiedEntity.class, "id", stopPlace, 3L);

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
        stream(stopPlaces, parkings, new ArrayList<>(), byteArrayOutputStream);
    }

    private void stream(List<StopPlace> stopPlaces, List<Parking> parkings, List<PathLink> pathLinks, ByteArrayOutputStream byteArrayOutputStream) throws InterruptedException, IOException, XMLStreamException, JAXBException {
        when(parkingRepository.scrollParkings()).thenReturn(parkings.iterator());
        when(parkingRepository.scrollParkings(anySetOf(Long.class))).thenReturn(parkings.iterator());
        when(parkingRepository.countResult(anySetOf(Long.class))).thenReturn(parkings.size());
        when(stopPlaceRepository.scrollStopPlaces(any())).thenReturn(stopPlaces.iterator());
        when(stopPlaceRepository.getDatabaseIds(any())).thenReturn(stopPlaces.stream().map(stopPlace -> getField(IdentifiedEntity.class, "id", stopPlace, Long.class)).collect(toSet()));
        when(pathLinkRepository.findAll()).thenReturn(pathLinks);
        when(pathLinkRepository.findByStopPlaceIds(anySetOf(Long.class))).thenReturn(pathLinks);

        streamingPublicationDelivery.stream(ExportParams.newExportParamsBuilder().build(), byteArrayOutputStream);
    }

    private void setField(Class clazz, String fieldName, Object instance, Object fieldValue) {
        try {
            Field field = clazz.getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(instance, fieldValue);
        } catch (IllegalAccessException|NoSuchFieldException e) {
            throw new RuntimeException("Cannot set field "+fieldName +" of "+instance, e);
        }
    }

    private <T> T getField(Class entityClass, String fieldName, Object instance, Class<T> clazz) {
        try {
            Field field = entityClass.getDeclaredField(fieldName);
            field.setAccessible(true);
            return clazz.cast(field.get(instance));
        } catch (IllegalAccessException|NoSuchFieldException e) {
            throw new RuntimeException("Cannot get field "+fieldName +" of "+instance, e);
        }
    }
}