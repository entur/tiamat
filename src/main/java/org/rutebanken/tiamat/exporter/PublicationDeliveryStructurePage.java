package org.rutebanken.tiamat.exporter;

import com.google.common.base.MoreObjects;
import org.rutebanken.netex.model.PublicationDeliveryStructure;

public class PublicationDeliveryStructurePage {

    public final int size;

    public PublicationDeliveryStructure publicationDeliveryStructure;

    public long totalElements;

    public boolean hasNext;

    public PublicationDeliveryStructurePage(PublicationDeliveryStructure publicationDeliveryStructure, int size, long totalElements, boolean hasNext) {
        this.publicationDeliveryStructure = publicationDeliveryStructure;
        this.totalElements = totalElements;
        this.size = size;
        this.hasNext = hasNext;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("publicationDeliveryStructure", publicationDeliveryStructure)
                .add("totalElements", totalElements)
                .add("hasNext", hasNext)
                .add("size", size)
                .toString();
    }
}
