package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = TiamatApplication.class)
@ActiveProfiles("geodb")
public class StavangerImportTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;


    @Test
    public void importStavanger() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("MOR:StopArea:15485753")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Skaret").withLang("no"))
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                        .withVersion("1")
                                        .withId("MOR:StopArea:1548612801")
                                        .withName(new MultilingualString().withValue("Skaret").withLang("no"))
                                        .withCentroid(new SimplePoint_VersionStructure().withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("62.799557598196465"))
                                                .withLongitude(new BigDecimal("7.328336965528884")))),
                                new Quay()
                                        .withId("MOR:StopArea:1548575301")
                                        .withVersion("1")
                                        .withName(new MultilingualString().withValue("Skaret").withLang("no"))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        // Exception should not have been thrown
        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(response);

        List<Quay> actualQuays = publicationDeliveryTestHelper.extractQuays(actualStopPlace);
        assertThat(actualQuays).isNotNull().as("quays should not be null");
    }
}
