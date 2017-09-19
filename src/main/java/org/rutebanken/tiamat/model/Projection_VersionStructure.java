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

import javax.xml.bind.JAXBElement;
import java.math.BigInteger;


public class Projection_VersionStructure
        extends DataManagedObjectStructure {

    protected TypeOfProjectionRefStructure typeOfProjectionRef;
    protected MultilingualStringEntity name;
    protected JAXBElement<? extends GroupOfPointsRefStructure> spatialFeatureRef;
    protected BigInteger order;

    public TypeOfProjectionRefStructure getTypeOfProjectionRef() {
        return typeOfProjectionRef;
    }

    public void setTypeOfProjectionRef(TypeOfProjectionRefStructure value) {
        this.typeOfProjectionRef = value;
    }

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public JAXBElement<? extends GroupOfPointsRefStructure> getSpatialFeatureRef() {
        return spatialFeatureRef;
    }

    public void setSpatialFeatureRef(JAXBElement<? extends GroupOfPointsRefStructure> value) {
        this.spatialFeatureRef = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
