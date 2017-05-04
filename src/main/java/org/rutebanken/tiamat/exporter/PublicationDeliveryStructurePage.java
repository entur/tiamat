package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.PublicationDeliveryStructure;

public class PublicationDeliveryStructurePage {


    public PublicationDeliveryStructure publicationDeliveryStructure;

    public boolean hasNext;

    public PublicationDeliveryStructurePage(PublicationDeliveryStructure publicationDeliveryStructure, boolean hasNext) {
        this.publicationDeliveryStructure = publicationDeliveryStructure;
        this.hasNext = hasNext;
    }
}
