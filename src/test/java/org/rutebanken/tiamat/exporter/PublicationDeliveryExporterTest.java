package org.rutebanken.tiamat.exporter;

import org.junit.Test;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.SiteFrame;
import org.rutebanken.netex.model.StopPlace;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.model.EmbeddableMultilingualString;
import org.rutebanken.tiamat.model.TariffZone;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;
import org.springframework.beans.factory.annotation.Autowired;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;

import static org.assertj.core.api.Assertions.assertThat;

public class PublicationDeliveryExporterTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryExporter publicationDeliveryExporter;

    @Autowired
    private PublicationDeliveryTestHelper publicationDeliveryTestHelper;

    @Test
    public void exportPublicationDeliveryWithStopPlace() throws JAXBException {
        org.rutebanken.tiamat.model.StopPlace stopPlace = new org.rutebanken.tiamat.model.StopPlace();
        stopPlace.setNetexId("NSR:StopPlace:987");
        stopPlaceRepository.save(stopPlace);

        PublicationDeliveryStructure publicationDeliveryStructure = publicationDeliveryExporter.exportAllStopPlaces();

        String expectedId = "NSR:StopPlace:987";
        StopPlace actual = publicationDeliveryTestHelper.findStopPlace(publicationDeliveryStructure, expectedId);
        assertThat(actual).isNotNull();
    }
}