

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class DeliveryVariants_RelStructure
    extends StrictContainmentAggregationStructure
{

    protected List<DeliveryVariant> deliveryVariant;

    public List<DeliveryVariant> getDeliveryVariant() {
        if (deliveryVariant == null) {
            deliveryVariant = new ArrayList<DeliveryVariant>();
        }
        return this.deliveryVariant;
    }

}
