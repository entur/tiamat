package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.TiamatIntegrationTest;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.TopographicPlacesExporter;
import org.rutebanken.tiamat.importer.modifier.StopPlacePreSteps;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;
import org.rutebanken.tiamat.netex.mapping.PublicationDeliveryHelper;
import org.rutebanken.tiamat.rest.netex.publicationdelivery.PublicationDeliveryTestHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class PublicationDeliveryImporterTest extends TiamatIntegrationTest {

    @Autowired
    private PublicationDeliveryHelper publicationDeliveryHelper;

    @SuppressWarnings("unchecked")
    @Test
    public void findSiteFrameFromCompositeFrame() {
        ObjectFactory objectFactory = new ObjectFactory();

        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(
                                        objectFactory.createCompositeFrame(
                                                new CompositeFrame()
                                                        .withFrames(new Frames_RelStructure()
                                                            .withCommonFrame(objectFactory.createCommonFrame(new SiteFrame()))))));

        SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);
        assertThat(siteFrame).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findSiteFrameFromCommonFrame() {
        ObjectFactory objectFactory = new ObjectFactory();

        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(
                                        objectFactory.createCommonFrame(new SiteFrame())));

        SiteFrame siteFrame = publicationDeliveryHelper.findSiteFrame(publicationDeliveryStructure);
        assertThat(siteFrame).isNotNull();
    }

}