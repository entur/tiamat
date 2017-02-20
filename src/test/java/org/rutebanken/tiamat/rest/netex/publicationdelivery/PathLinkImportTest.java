package org.rutebanken.tiamat.rest.netex.publicationdelivery;

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.netex.model.PathLink;
import org.rutebanken.netex.model.PlaceRef;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.CommonSpringBootTest;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class PathLinkImportTest extends CommonSpringBootTest {

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    private DatatypeFactory datatypeFactory = DatatypeFactory.newInstance();

    public PathLinkImportTest() throws DatatypeConfigurationException {
    }

    @Test
    public void publicationDeliveryWithDuplicateStopPlace() throws Exception {

        StopPlace fromStopPlace = new StopPlace()
                .withId("RUT:StopPlace:123123")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9"))
                                .withLongitude(new BigDecimal("71"))));

        StopPlace toStopPlace = new StopPlace()
                .withId("RUT:StopPlace:321654")
                .withVersion("1")
                .withCentroid(new SimplePoint_VersionStructure()
                        .withLocation(new LocationStructure()
                                .withLatitude(new BigDecimal("9.6"))
                                .withLongitude(new BigDecimal("76"))));

        Duration duration = datatypeFactory.newDuration(10000);

        PathLink netexPathLink = new PathLink()
                .withId("NRI:ConnectionLink:762130479_762130479")
                .withVersion("1")
                .withAllowedUse(PathDirectionEnumeration.TWO_WAY)
                .withTransferDuration(new TransferDurationStructure()
                        .withDefaultDuration(duration))
                .withFrom(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRefStructure()
                                                .withRef(fromStopPlace.getId())
                                                .withNameOfMemberClass(fromStopPlace.getClass().getSimpleName())))
                .withTo(
                        new PathLinkEndStructure()
                                .withPlaceRef(
                                        new PlaceRefStructure()
                                                .withRef(toStopPlace.getId())
                                                .withVersion("1")
                                                .withNameOfMemberClass(toStopPlace.getClass().getSimpleName())));



        PublicationDeliveryStructure publicationDelivery = publicationDeliveryTestHelper.createPublicationDeliveryWithStopPlace(fromStopPlace, toStopPlace);
        publicationDeliveryTestHelper.addPathLinks(publicationDelivery, netexPathLink);

        PublicationDeliveryStructure response = publicationDeliveryTestHelper.postAndReturnPublicationDelivery(publicationDelivery);

        List<PathLink> result = publicationDeliveryTestHelper.extractPathLinks(response);
        assertThat(result).as("Expecting path link in return").hasSize(1);
        assertThat(result.get(0).getAllowedUse()).isEqualTo(netexPathLink.getAllowedUse());
        assertThat(result.get(0).getFrom().getPlaceRef().getNameOfMemberClass()).isEqualTo(fromStopPlace.getClass().getSimpleName());
        assertThat(result.get(0).getTo().getPlaceRef().getNameOfMemberClass()).isEqualTo(toStopPlace.getClass().getSimpleName());
        assertThat(result.get(0).getTransferDuration().getDefaultDuration()).isEqualTo(duration);
    }
}
