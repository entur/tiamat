package org.rutebanken.tiamat.exporter;

import org.rutebanken.netex.model.PublicationDeliveryStructure;

public class PublicationDeliveryStructurePage {

    public PublicationDeliveryStructure publicationDeliveryStructure;

    public long totalElements;

    public boolean hasNext;

    public PublicationDeliveryStructurePage(PublicationDeliveryStructure publicationDeliveryStructure, long totalElements, boolean hasNext) {
        this.publicationDeliveryStructure = publicationDeliveryStructure;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
    }
}
