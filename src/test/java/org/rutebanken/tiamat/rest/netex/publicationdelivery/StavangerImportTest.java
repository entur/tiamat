package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Import tests for different cases in Stavanger.
 */
public class StavangerImportTest  extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;


    /**
     * Import stop place with hpl. and number in the name.
     * The number should be moved to the Quay name.
     * Stop place does not have centroid. This causes tests to not interfer with each other.
     */
    @Test
    public void importStavangerWithHplNumbering() throws Exception {

        StopPlace stopPlace = new StopPlace()
                .withId("KOL:StopArea:987654")
                .withVersion("1")
                .withName(new MultilingualString().withValue("Stavanger hpl. 12").withLang("no"))
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                        .withVersion("1")
                                        .withId("KOL:StopArea:87654")
                                        .withName(new MultilingualString().withValue("Stavanger hpl. 12").withLang("no"))
                                        .withCentroid(new SimplePoint_VersionStructure().withLocation(new LocationStructure()
                                                .withLatitude(new BigDecimal("58.966910"))
                                                .withLongitude(new BigDecimal("5.732949"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(response);

        assertThat(actualStopPlace.getName().getValue()).isEqualTo("Stavanger");

        List<Quay> actualQuays = publicationDeliveryTestHelper.extractQuays(actualStopPlace);
        assertThat(actualQuays).isNotNull().as("quays should not be null");
        assertThat(actualQuays.get(0).getPublicCode()).describedAs("Quay name should not be null").isNotNull();
        assertThat(actualQuays.get(0).getPublicCode()).isEqualTo("12");
    }

    /**
     * Import stop place with spor and number in the name.
     * The number should be moved to the Quay name.
     */
    @Test
    public void importStopWithSporAndNumbering() throws Exception {

        final String originalStopPlaceName = "Stavanger spor 2";

        StopPlace stopPlace = new StopPlace()
                .withId("KOL:StopArea:11032650")
                .withVersion("1")
                .withName(new MultilingualString().withValue(originalStopPlaceName).withLang("no"))
                .withStopPlaceType(StopTypeEnumeration.ONSTREET_BUS)
                .withQuays(new Quays_RelStructure()
                        .withQuayRefOrQuay(new Quay()
                                .withVersion("1")
                                .withId("KOL:StopArea:1103265001")
                                .withName(new MultilingualString().withValue(originalStopPlaceName).withLang("no"))
                                .withCentroid(new SimplePoint_VersionStructure().withLocation(new LocationStructure()
                                        .withLatitude(new BigDecimal("58.966910"))
                                        .withLongitude(new BigDecimal("5.732949"))))));

        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(stopPlace);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        StopPlace actualStopPlace = publicationDeliveryTestHelper.findFirstStopPlace(response);

        assertThat(actualStopPlace.getName().getValue()).describedAs("Stop place name").isEqualTo("Stavanger");

        List<Quay> actualQuays = publicationDeliveryTestHelper.extractQuays(actualStopPlace);
        assertThat(actualQuays).isNotNull().as("quays should not be null");
        assertThat(actualQuays.get(0).getPublicCode()).describedAs("Quay name should not be null").isNotNull();
        assertThat(actualQuays.get(0).getPublicCode()).isEqualTo("2");
    }
}
