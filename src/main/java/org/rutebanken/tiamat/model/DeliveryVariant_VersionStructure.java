package org.rutebanken.tiamat.model;

import java.math.BigInteger;


public class DeliveryVariant_VersionStructure
        extends DataManagedObjectStructure {

    protected VersionOfObjectRefStructure parentRef;
    protected DeliveryVariantTypeEnumeration deliveryVariantMediaType;
    protected TypeOfDeliveryVariantRefStructure typeOfDeliveryVariantRef;
    protected MultilingualStringEntity variantText;
    protected BigInteger order;

    public VersionOfObjectRefStructure getParentRef() {
        return parentRef;
    }

    public void setParentRef(VersionOfObjectRefStructure value) {
        this.parentRef = value;
    }

    public DeliveryVariantTypeEnumeration getDeliveryVariantMediaType() {
        return deliveryVariantMediaType;
    }

    public void setDeliveryVariantMediaType(DeliveryVariantTypeEnumeration value) {
        this.deliveryVariantMediaType = value;
    }

    public TypeOfDeliveryVariantRefStructure getTypeOfDeliveryVariantRef() {
        return typeOfDeliveryVariantRef;
    }

    public void setTypeOfDeliveryVariantRef(TypeOfDeliveryVariantRefStructure value) {
        this.typeOfDeliveryVariantRef = value;
    }

    public MultilingualStringEntity getVariantText() {
        return variantText;
    }

    public void setVariantText(MultilingualStringEntity value) {
        this.variantText = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
