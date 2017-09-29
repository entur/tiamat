/*
 * Licensed under the EUPL, Version 1.2 or – as soon they will be approved by
 * the European Commission - subsequent versions of the EUPL (the "Licence");
 * You may not use this work except in compliance with the Licence.
 * You may obtain a copy of the Licence at:
 *
 *   https://joinup.ec.europa.eu/software/page/eupl
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the Licence is distributed on an "AS IS" basis,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Licence for the specific language governing permissions and
 * limitations under the Licence.
 */

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
