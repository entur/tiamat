package org.rutebanken.tiamat.exporter;

import jakarta.xml.bind.JAXBElement;
import org.rutebanken.netex.model.DataManagedObjectStructure;
import org.rutebanken.netex.model.MultilingualString;
import org.rutebanken.netex.model.ObjectFactory;
import org.rutebanken.netex.model.PublicationDeliveryStructure;
import org.rutebanken.netex.model.PurposeOfGrouping;
import org.rutebanken.netex.model.ResourceFrame;
import org.rutebanken.tiamat.netex.id.ValidPrefixList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class PublicationDeliveryCreator {

    private static final Logger logger = LoggerFactory.getLogger(PublicationDeliveryCreator.class);
    private final String publicationDeliveryId;
    private final ValidPrefixList validPrefixList;

    public PublicationDeliveryCreator(@Value("${netex.profile.version:1.12:NO-NeTEx-stops:1.4}") String publicationDeliveryId,
                                      ValidPrefixList validPrefixList) {
        this.publicationDeliveryId = publicationDeliveryId;
        this.validPrefixList = validPrefixList;
    }


    public PublicationDeliveryStructure createPublicationDelivery() {
        return new PublicationDeliveryStructure()
                .withVersion(publicationDeliveryId)
                .withPublicationTimestamp(LocalDateTime.now())
                .withParticipantRef(validPrefixList.getValidNetexPrefix());
    }

    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();
        publicationDeliveryStructure.withDataObjects(
                new PublicationDeliveryStructure.DataObjects()
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame))
                        .withCompositeFrameOrCommonFrame(new ObjectFactory().createResourceFrame(createResourceFrame()))
        );

        logger.info("Returning publication delivery {} with site frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame,
                                                                  org.rutebanken.netex.model.ServiceFrame serviceFrame,
                                                                  org.rutebanken.netex.model.FareFrame fareFrame,
                                                                  ResourceFrame netexResourceFrame
    ) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();

        publicationDeliveryStructure.withDataObjects
                (
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createServiceFrame(serviceFrame))
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame))
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createFareFrame(fareFrame))
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createResourceFrame(netexResourceFrame))
                );

        logger.info("Returning publication delivery {} with site frame and  service frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame,
                                                                  org.rutebanken.netex.model.ServiceFrame serviceFrame,
                                                                  org.rutebanken.netex.model.FareFrame fareFrame
    ) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();

        publicationDeliveryStructure.withDataObjects
                (
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createServiceFrame(serviceFrame))
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame))
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createFareFrame(fareFrame))
                );

        logger.info("Returning publication delivery {} with site frame and  service frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame,
                                                                  org.rutebanken.netex.model.ServiceFrame serviceFrame,
                                                                  ResourceFrame netexResourceFrame
    ) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();

        publicationDeliveryStructure.withDataObjects
                (
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createServiceFrame(serviceFrame))
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame))
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createResourceFrame(netexResourceFrame))
                );

        logger.info("Returning publication delivery {} with site frame and  service frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }

    public PublicationDeliveryStructure createPublicationDelivery(org.rutebanken.netex.model.SiteFrame siteFrame,
                                                                  org.rutebanken.netex.model.ServiceFrame serviceFrame
    ) {
        PublicationDeliveryStructure publicationDeliveryStructure = createPublicationDelivery();

        publicationDeliveryStructure.withDataObjects
                (
                        new PublicationDeliveryStructure.DataObjects()
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createServiceFrame(serviceFrame))
                                .withCompositeFrameOrCommonFrame(new ObjectFactory().createSiteFrame(siteFrame))
                );

        logger.info("Returning publication delivery {} with site frame and  service frame", publicationDeliveryStructure);
        return publicationDeliveryStructure;
    }



    private ResourceFrame createResourceFrame() {
        List<JAXBElement<? extends DataManagedObjectStructure>> purposeOfGroupingList = new ArrayList<>();
        final PurposeOfGrouping purposeOfGrouping = new ObjectFactory().createPurposeOfGrouping().withId("NSR:PurposeOfGrouping:3").withName(new MultilingualString().withValue("generalization")).withVersion("1");
        final JAXBElement<PurposeOfGrouping> purposeOfGroupingJAXBElement= new ObjectFactory().createPurposeOfGrouping(purposeOfGrouping);
        purposeOfGroupingList.add(purposeOfGroupingJAXBElement);


        return new ResourceFrame().withId("NSR:RescourceFrame:1").withVersion("1")
                .withTypesOfValue(new ObjectFactory()
                        .createTypesOfValueInFrame_RelStructure().withValueSetOrTypeOfValue(purposeOfGroupingList));
    }



}
