package org.rutebanken.tiamat.exporter;

import com.google.common.base.MoreObjects;
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

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("publicationDeliveryStructure", publicationDeliveryStructure)
                .add("totalElements", totalElements)
                .add("hasNext", hasNext)
                .toString();
    }
}
