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


public class TrainComponent_DerivedViewStructure
        extends DerivedViewStructure {

    protected TrainComponentRefStructure trainComponentRef;
    protected MultilingualStringEntity label;
    protected MultilingualStringEntity description;
    protected TrainElementRefStructure trainElementRef;

    protected TrainElementTypeEnumeration trainElementType;
    protected BigInteger order;

    public TrainComponentRefStructure getTrainComponentRef() {
        return trainComponentRef;
    }

    public void setTrainComponentRef(TrainComponentRefStructure value) {
        this.trainComponentRef = value;
    }

    public MultilingualStringEntity getLabel() {
        return label;
    }

    public void setLabel(MultilingualStringEntity value) {
        this.label = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public TrainElementRefStructure getTrainElementRef() {
        return trainElementRef;
    }

    public void setTrainElementRef(TrainElementRefStructure value) {
        this.trainElementRef = value;
    }


    public TrainElementTypeEnumeration getTrainElementType() {
        return trainElementType;
    }

    public void setTrainElementType(TrainElementTypeEnumeration value) {
        this.trainElementType = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
