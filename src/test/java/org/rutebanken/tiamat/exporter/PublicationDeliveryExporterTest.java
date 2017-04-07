package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicationDeliveryExporterTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryExporter publicationDeliveryExporter;

    @Test
    public void exportPublicationDeliveryWithStopPlace() throws JAXBException {
        org.rutebanken.tiamat.model.StopPlace stopPlace = new org.rutebanken.tiamat.model.StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:987");
        stopPlaceRepository.save(stopPlace);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportAllStopPlaces();

        String expectedId = "NSR:StopPlace:987";
        StopPlace actual = findStopPlace(publicationDeliveryStructure, expectedId);
        assertThat(actual).isNotNull();
    }

    private StopPlace findStopPlace(PublicationDeliveryStructure publicationDeliveryStructure, String stopPlaceId) {
        return publicationDeliveryStructure.getDataObjects()
                .getCompositeFrameOrCommonFrame()
                .stream()
                .map(JAXBElement::getValue)
                .filter(commonVersionFrameStructure -> commonVersionFrameStructure instanceof SiteFrame)
                .flatMap(commonVersionFrameStructure -> ((SiteFrame) commonVersionFrameStructure).getStopPlaces().getStopPlace().stream())
                .peek(System.out::println)
                .filter(stopPlace -> stopPlace.getId().equals(stopPlaceId))
                .findFirst().get();
    }


}