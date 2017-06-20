package org.rutebanken.tiamat.rest.netex.publicationdelivery;


import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.importer.PublicationDeliveryParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.rutebanken.tiamat.netex.mapping.mapper.NetexIdMapper.ORIGINAL_ID_KEY;

import static javax.xml.bind.JAXBContext.newInstance;

@Component
public class PublicationDeliveryTestHelper {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryTestHelper.class);

    private static final JAXBContext jaxbContext;

    static {
        try {
            jaxbContext = newInstance(PublicationDeliveryStructure.class);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
    }

    @Autowired
    private ImportResource importResource;

    public PublicationDeliveryStructure createPublicationDeliveryTopographicPlace(TopographicPlace... topographicPlace) {
        SiteFrame siteFrame = siteFrame();
        siteFrame.withTopographicPlaces(new TopographicPlacesInFrame_RelStructure().withTopographicPlace(topographicPlace));
        return publicationDelivery(siteFrame);
    }

    public PublicationDeliveryStructure publicationDelivery(SiteFrame siteFrame) {
        return new PublicationDeliveryStructure()
                .withPublicationTimestamp(OffsetDateTime.now())
                .withVersion("1")
                .withParticipantRef("test")
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));
    }

    public SiteFrame siteFrame() {
        SiteFrame siteFrame = new SiteFrame();
        siteFrame.setVersion("1");
        siteFrame.setId(UUID.randomUUID().toString());
        return siteFrame;
    }


    public PublicationDeliveryStructure createPublicationDeliveryWithStopPlace(StopPlace... stopPlace) {
        SiteFrame siteFrame = siteFrame();
        siteFrame.withStopPlaces(new StopPlacesInFrame_RelStructure()
                .withStopPlace(stopPlace));

        return publicationDelivery(siteFrame);
    }

    public void addPathLinks(PublicationDeliveryStructure publicationDeliveryStructure, PathLink... pathLink) {
        findSiteFrame(publicationDeliveryStructure)
                .withPathLinks(new PathLinksInFrame_RelStructure().withPathLink(pathLink));
    }

    public void hasOriginalId(String expectedId, DataManagedObjectStructure object) {
        assertThat(object).isNotNull();
        assertThat(object.getKeyList()).isNotNull();
        List<String> list = object.getKeyList().getKeyValue()
                .stream()
                .peek(keyValueStructure -> System.out.println(keyValueStructure))
                .filter(keyValueStructure -> keyValueStructure.getKey().equals(ORIGINAL_ID_KEY))
                .map(keyValueStructure -> keyValueStructure.getValue())
                .map(value -> value.split(","))
                .flatMap(values -> Stream.of(values))
                .filter(value -> value.equals(expectedId))
                .collect(Collectors.toList());
        assertThat(list).as("Matching original ID "+expectedId).hasSize(1);
    }

    public List<StopPlace> extractStopPlaces(Response response) throws IOException, JAXBException {
        return extractStopPlaces(fromResponse(response));
    }

    public List<StopPlace> extractStopPlaces(PublicationDeliveryStructure publicationDeliveryStructure) {
        return extractStopPlaces(publicationDeliveryStructure, true);
    }

    public List<StopPlace> extractStopPlaces(PublicationDeliveryStructure publicationDeliveryStructure, boolean verifyNotNull) {
        SiteFrame siteFrame = findSiteFrame(publicationDeliveryStructure);
        if(verifyNotNull) {
            assertThat(siteFrame.getStopPlaces()).as("Site frame stop places").isNotNull();
            assertThat(siteFrame.getStopPlaces().getStopPlace()).as("Site frame stop places getStopPlace").isNotNull();
        } else if(siteFrame.getStopPlaces() == null || siteFrame.getStopPlaces().getStopPlace() == null) {
            return new ArrayList<>();
        }
        return siteFrame.getStopPlaces().getStopPlace();
    }

    public List<PathLink> extractPathLinks(PublicationDeliveryStructure publicationDeliveryStructure) {

        SiteFrame siteFrame = findSiteFrame(publicationDeliveryStructure);
        if(siteFrame.getPathLinks() != null && siteFrame.getPathLinks().getPathLink() != null) {
            return siteFrame.getPathLinks().getPathLink();
        } else {
            return new ArrayList<>();
        }
    }

    public List<TopographicPlace> extractTopographicPlace(PublicationDeliveryStructure publicationDeliveryStructure) {

        SiteFrame siteFrame = findSiteFrame(publicationDeliveryStructure);
        if(siteFrame.getTopographicPlaces() != null && siteFrame.getTopographicPlaces().getTopographicPlace() != null) {
            return siteFrame.getTopographicPlaces().getTopographicPlace();
        } else {
            return new ArrayList<>();
        }
    }

    public List<Quay> extractQuays(StopPlace stopPlace) {
        return stopPlace
                .getQuays()
                .getQuayRefOrQuay()
                .stream()
                .filter(object -> object instanceof Quay)
                .map(object -> ((Quay) object))
                .collect(toList());
    }

    public StopPlace findFirstStopPlace(PublicationDeliveryStructure publicationDeliveryStructure) {
        return publicationDeliveryStructure.getDataObjects()
                .getCompositeFrameOrCommonFrame()
                .stream()
                .map(JAXBElement::getValue)
                .filter(commonVersionFrameStructure -> commonVersionFrameStructure instanceof SiteFrame)
                .flatMap(commonVersionFrameStructure -> ((SiteFrame) commonVersionFrameStructure).getStopPlaces().getStopPlace().stream())
                .findFirst().get();
    }

    public PublicationDeliveryStructure postAndReturnPublicationDelivery(PublicationDeliveryStructure publicationDeliveryStructure) throws JAXBException, IOException, SAXException {
        return postAndReturnPublicationDelivery(publicationDeliveryStructure, null);
    }

    public PublicationDeliveryStructure postAndReturnPublicationDelivery(PublicationDeliveryStructure publicationDeliveryStructure, PublicationDeliveryParams publicationDeliveryParams) throws JAXBException, IOException, SAXException {
        Response response = postPublicationDelivery(publicationDeliveryStructure, publicationDeliveryParams);

        if(! (response.getEntity() instanceof StreamingOutput)) {
            throw new RuntimeException("Response is not instance of streaming output: "+response);
        }
        return fromResponse(response);
    }

    public PublicationDeliveryStructure postAndReturnPublicationDelivery(String publicationDeliveryXml) throws JAXBException, IOException, SAXException {
        return postAndReturnPublicationDelivery(publicationDeliveryXml, null);
    }

    public PublicationDeliveryStructure postAndReturnPublicationDelivery(String publicationDeliveryXml, PublicationDeliveryParams publicationDeliveryParams) throws JAXBException, IOException, SAXException {

        InputStream stream = new ByteArrayInputStream(publicationDeliveryXml.getBytes(StandardCharsets.UTF_8));

        Response response = importResource.importPublicationDelivery(stream, publicationDeliveryParams);

        assertThat(response.getStatus()).isEqualTo(200);

        return fromResponse(response);
    }

    public PublicationDeliveryStructure fromResponse(Response response) throws IOException, JAXBException {
        StreamingOutput output = (StreamingOutput) response.getEntity();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        output.write(outputStream);

        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setEventHandler(new javax.xml.bind.helpers.DefaultValidationEventHandler());

        byte[] bytes = outputStream.toByteArray();
        logger.info("Printing received publication delivery\n--------------\n{}\n--------------", new String(bytes));

        InputStream inputStream = new ByteArrayInputStream(bytes);
        JAXBElement element = (JAXBElement) unmarshaller.unmarshal(inputStream);
        return (PublicationDeliveryStructure) element.getValue();
    }

    public Response postPublicationDelivery(PublicationDeliveryStructure publicationDeliveryStructure, PublicationDeliveryParams publicationDeliveryParams) throws JAXBException, IOException, SAXException {
        Marshaller marshaller = jaxbContext.createMarshaller();

        JAXBElement<PublicationDeliveryStructure> jaxPublicationDelivery = new ObjectFactory().createPublicationDelivery(publicationDeliveryStructure);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        marshaller.marshal(jaxPublicationDelivery, outputStream);
        InputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());

        return importResource.importPublicationDelivery(inputStream, publicationDeliveryParams);
    }

    public SiteFrame findSiteFrame(PublicationDeliveryStructure publicationDelivery) {

        List<JAXBElement<? extends Common_VersionFrameStructure>> compositeFrameOrCommonFrame = publicationDelivery.getDataObjects().getCompositeFrameOrCommonFrame();

        Optional<SiteFrame> optionalSiteframe = compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof SiteFrame)
                .map(element -> (SiteFrame) element.getValue())
                .findFirst();

        if (optionalSiteframe.isPresent()) {
            logger.info("Found site frame from compositeFrameOrCommonFrame {}", optionalSiteframe.get().getStopPlaces());
            return optionalSiteframe.get();
        }

        return compositeFrameOrCommonFrame
                .stream()
                .filter(element -> element.getValue() instanceof CompositeFrame)
                .map(element -> (CompositeFrame) element.getValue())
                .map(compositeFrame -> compositeFrame.getFrames())
                .flatMap(frames -> frames.getCommonFrame().stream())
                .filter(jaxbElement -> jaxbElement.getValue() instanceof SiteFrame)
                .map(jaxbElement -> (SiteFrame) jaxbElement.getValue())
                .findAny().get();
    }
}
