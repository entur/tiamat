package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.netex.client.PublicationDeliveryClient;
import org.rutebanken.netex.model.*;
import org.xml.sax.SAXException;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test implemented to try to reproduce an issue with hibernate/spring boot/postgres where duplicate keys are tried inserted to the database.
 * This test requires tiamat to be running in another jvm
 * See https://rutebanken.atlassian.net/browse/NRP-735
 */
public class ReproduceDuplicateKeysException {

    private static final String TIAMAT_URL = "http://localhost:1997/jersey/publication_delivery";

    @SuppressWarnings("unchecked")
    @Ignore
    @Test
    public void reproduceDuplicateKeyIssue() throws JAXBException, IOException, SAXException, InterruptedException {
        final int threads = 20;
        final int publicationDeliveries = 100;
        final int eachPublicationDeliverySentTimes = 5;

        final int stopPlacesPerPublicationDelivery = 20;
        final PublicationDeliveryClient client = new PublicationDeliveryClient(TIAMAT_URL);
        final ExecutorService executorService = Executors.newFixedThreadPool(threads);

        final AtomicInteger publicationDeliveriesSent = new AtomicInteger();
        final AtomicInteger exceptionsReceived = new AtomicInteger();

        for (int i = 0; i < publicationDeliveries; i++) {

            SiteFrame siteFrame = createSiteFrame();
            siteFrame.withStopPlaces(new StopPlacesInFrame_RelStructure()
                    .withStopPlace(createStopPlacesWithQuays(i % 2 == 0 ? i : 0, stopPlacesPerPublicationDelivery)));

            PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                    .withPublicationTimestamp(OffsetDateTime.now())
                    .withParticipantRef("NSR")
                    .withVersion("any")
                    .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                            .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));


            for (int j = 0; j < eachPublicationDeliverySentTimes; j++) {
                executorService.submit(() -> {
                    try {
                        client.sendPublicationDelivery(publicationDelivery);
                        publicationDeliveriesSent.incrementAndGet();
                    } catch (JAXBException | IOException | SAXException e) {
                        exceptionsReceived.incrementAndGet();
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.MINUTES);
        assertThat(publicationDeliveriesSent.get()).isEqualTo(publicationDeliveries * eachPublicationDeliverySentTimes).describedAs("excpected number of publication deliveries sent successfully");
        assertThat(exceptionsReceived.get()).isZero().as("no exceptions expected");
        System.out.println("done");

    }

    @Ignore
    @Test
    public void reproduceDuplicateKeyIssue2() throws JAXBException, InterruptedException, IOException, SAXException {
        final PublicationDeliveryClient client = new PublicationDeliveryClient(TIAMAT_URL);

        LocationStructure location = randomCoordinates();
        StopPlace stopPlace1 = new StopPlace()
                .withId("XYZ:StopPlace:1")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Stop place "))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(location))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(
                                new Quay()
                                        .withName(new MultilingualString().withValue("Quay 1"))
                                        .withId("XYZ:Quay1")
                                        .withVersion("1")
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withLocation(location)),
                                new Quay()
                                        .withName(new MultilingualString().withValue("Quay 2"))
                                        .withId("XYZ:Quay2")
                                        .withVersion("1")
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withLocation(location))));

        StopPlace stopPlace2 = new StopPlace()
                .withId("XYZ:StopPlace:1")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Stop place "))
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(location))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(
                                new Quay()
                                        .withName(new MultilingualString().withValue("Quay 1"))
                                        .withId("123")
                                        .withVersion("1")
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withLocation(location)),
                                new Quay()
                                        .withName(new MultilingualString().withValue("Quay 2"))
                                        .withId("1234")
                                        .withVersion("1")
                                        .withCentroid(new SimplePoint_VersionStructure()
                                                .withLocation(location))));

        SiteFrame siteFrame = createSiteFrame();
        siteFrame.withStopPlaces(new StopPlacesInFrame_RelStructure()
                .withStopPlace(stopPlace1));

        PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                .withParticipantRef("NSR")
                .withPublicationTimestamp(OffsetDateTime.now())
                .withVersion("any")
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));

        client.sendPublicationDelivery(publicationDelivery);


        SiteFrame siteFrame2 = createSiteFrame();
        siteFrame2.withStopPlaces(new StopPlacesInFrame_RelStructure()
                .withStopPlace(stopPlace2));

        PublicationDeliveryStructure publicationDelivery2 = new PublicationDeliveryStructure()
                .withParticipantRef("NSR")
                .withVersion("any")
                .withPublicationTimestamp(OffsetDateTime.now())
                .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame2)));

        client.sendPublicationDelivery(publicationDelivery2);
    }

    public SiteFrame createSiteFrame() {
        SiteFrame siteFrame = new SiteFrame();
        siteFrame.setVersion("any");
        siteFrame.setId("1");
        return  siteFrame;
    }

    public List<StopPlace> createStopPlacesWithQuays(int salt, int numberOfstopPlaces) {

        List<StopPlace> stopPlaces = new ArrayList<>(numberOfstopPlaces);
        for (int i = 0; i < numberOfstopPlaces; i++) {

            LocationStructure location = randomCoordinates();
            StopPlace stopPlace = new StopPlace()
                    .withId("XYZ:StopPlace" + salt + ":" + i)
                    .withVersion("1")
                    .withName(new MultilingualString().withValue("Stop place " + i + " pd" + salt))
                    .withCentroid(new SimplePoint_VersionStructure()
                            .withLocation(location))
                    .withQuays(new Quays_RelStructure()
                            .withQuayRefOrQuay(new Quay()
                                            .withName(new MultilingualString().withValue("Quay " + i + " pd" + salt))
                                            .withId("XYZ:Quay" + salt + ":" + i)
                                            .withVersion("1")
                                            .withCentroid(new SimplePoint_VersionStructure()
                                                    .withLocation(location)),
                                    new Quay()
                                            .withName(new MultilingualString().withValue("someother Quay " + i + " pd" + salt))
                                            .withId("XYZ:Quay" + salt + "two:" + i)
                                            .withVersion("1")
                                            .withCentroid(new SimplePoint_VersionStructure()
                                                    .withLocation(location))));

            stopPlaces.add(stopPlace);
        }
        return stopPlaces;
    }

    private final Random random = new Random();
    private final double latitudeMin = 40.0;
    private final double latitudeMax = 90.0;

    private final double longitudeMin = 50.0;
    private final double longitudeMax = 60.0;

    public LocationStructure randomCoordinates() {
        double latitude = latitudeMin + (latitudeMax - latitudeMin) * random.nextDouble();
        double longitude = longitudeMin + (longitudeMax - longitudeMin) * random.nextDouble();

        return new LocationStructure()
                .withLatitude(new BigDecimal(String.valueOf(latitude)))
                .withLongitude(new BigDecimal(String.valueOf(longitude)));
    }
}
