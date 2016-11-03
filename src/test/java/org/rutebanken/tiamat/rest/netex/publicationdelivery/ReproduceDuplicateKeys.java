package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Ignore;
import org.junit.Test;
import org.rutebanken.netex.client.PublicationDeliveryClient;
import org.rutebanken.netex.model.*;

import javax.xml.bind.JAXBException;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.TimeUnit;

/**
 * Test implemented to try to reproduce an issue with hibernate/spring boot/postgres where duplicate keys are tried inserted to the database.
 */
public class ReproduceDuplicateKeys {

    @Ignore
    @Test
    public void reproduceDuplicateKeyIssue() throws JAXBException, InterruptedException {
        final int threads = 40;
        final int publicationDeliveries = 100;
        final int stopPlacesPerPublicationDelivery = 100;
        final PublicationDeliveryClient client = new PublicationDeliveryClient("http://localhost:1997/jersey/publication_delivery");
        final ExecutorService executorService = Executors.newFixedThreadPool(threads);


        for(int i = 0; i < publicationDeliveries; i++) {

            SiteFrame siteFrame = new SiteFrame();
            siteFrame.withStopPlaces(new StopPlacesInFrame_RelStructure()
                    .withStopPlace(createStopPlacesWithQuays(i%2==0?i:0, stopPlacesPerPublicationDelivery)));

            PublicationDeliveryStructure publicationDelivery = new PublicationDeliveryStructure()
                    .withDataObjects(new PublicationDeliveryStructure.DataObjects()
                            .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame)));


            executorService.submit(() -> {
                try {
                    client.sendPublicationDelivery(publicationDelivery);
                } catch (JAXBException|IOException e) {
                  throw new RuntimeException(e);
                }
            });

        }

        executorService.shutdown();
        executorService.awaitTermination(100, TimeUnit.MINUTES);
        System.out.println("done");

    }

    public List<StopPlace> createStopPlacesWithQuays(int salt, int numberOfstopPlaces) {

        List<StopPlace> stopPlaces = new ArrayList<>(numberOfstopPlaces);
        for (int i = 0; i < numberOfstopPlaces; i++) {
            StopPlace stopPlace = new StopPlace()
                    .withId("XYZ:StopPlace"+salt+":"+i)
                    .withName(new MultilingualString().withValue("Stop place "+i+ " pd" + salt))
                    .withCentroid(new SimplePoint_VersionStructure()
                            .withLocation(new LocationStructure()
                                    .withLatitude(new BigDecimal("9."+i))
                                    .withLongitude(new BigDecimal("71."+i))))
                    .withQuays(new Quays_RelStructure()
                            .withQuayRefOrQuay(new Quay()
                                    .withName(new MultilingualString().withValue("Quay "+i + " pd" + salt))
                                    .withId("XYZ:Quay"+salt+":"+i)
                                    .withCentroid(new SimplePoint_VersionStructure()
                                            .withLocation(new LocationStructure()
                                                    .withLatitude(new BigDecimal("9."+i))
                                                    .withLongitude(new BigDecimal("71."+i))))));
            stopPlaces.add(stopPlace);
        }
        return stopPlaces;
    }
}
