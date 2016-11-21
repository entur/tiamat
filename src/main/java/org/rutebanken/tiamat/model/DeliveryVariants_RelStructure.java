package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class DeliveryVariants_RelStructure
        extends StrictContainmentAggregationStructure {

    protected List<DeliveryVariant> deliveryVariant;

    public List<DeliveryVariant> getDeliveryVariant() {
        if (deliveryVariant == null) {
            deliveryVariant = new ArrayList<DeliveryVariant>();
        }
        return this.deliveryVariant;
    }

}
