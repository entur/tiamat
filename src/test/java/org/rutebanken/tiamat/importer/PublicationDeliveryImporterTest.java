package org.rutebanken.tiamat.importer;

import org.junit.Test;
import org.rutebanken.netex.model.*;
import org.rutebanken.tiamat.exporter.PublicationDeliveryExporter;
import org.rutebanken.tiamat.exporter.TopographicPlacesExporter;
import org.rutebanken.tiamat.importer.modifier.StopPlacePreSteps;
import org.rutebanken.tiamat.netex.mapping.NetexMapper;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;

public class PublicationDeliveryImporterTest {

    PublicationDeliveryImporter publicationDeliveryImporter = new PublicationDeliveryImporter(mock(NetexMapper.class), mock(TransactionalStopPlacesImporter.class), mock(PublicationDeliveryExporter.class), mock(StopPlacePreSteps.class), mock(PathLinksImporter.class), new TopographicPlacesExporter());

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

        SiteFrame siteFrame = publicationDeliveryImporter.findSiteFrame(publicationDeliveryStructure);
        assertThat(siteFrame).isNotNull();
    }

    @SuppressWarnings("unchecked")
    @Test
    public void findSiteFrameFromCommonFrame() {
        ObjectFactory objectFactory = new ObjectFactory();
        PublicationDeliveryImporter publicationDeliveryImporter = new PublicationDeliveryImporter(mock(NetexMapper.class), mock(TransactionalStopPlacesImporter.class), mock(PublicationDeliveryExporter.class), mock(StopPlacePreSteps.class), mock(PathLinksImporter.class), new TopographicPlacesExporter());


        PublicationDeliveryStructure publicationDeliveryStructure = new PublicationDeliveryStructure()
                .withDataObjects(
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(
                                        objectFactory.createCommonFrame(new SiteFrame())));

        SiteFrame siteFrame = publicationDeliveryImporter.findSiteFrame(publicationDeliveryStructure);
        assertThat(siteFrame).isNotNull();
    }

}