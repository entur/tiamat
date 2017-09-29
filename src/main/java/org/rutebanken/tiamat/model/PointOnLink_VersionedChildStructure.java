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
import java.math.BigDecimal;
import java.math.BigInteger;


public class PointOnLink_VersionedChildStructure
        extends VersionedChildStructure {

    protected MultilingualStringEntity name;
    protected LinkRefStructure linkRef;
    protected BigDecimal distanceFromStart;
    protected JAXBElement<? extends PointRefStructure> pointRef;
    protected JAXBElement<? extends Point> point;
    protected BigInteger order;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public LinkRefStructure getLinkRef() {
        return linkRef;
    }

    public void setLinkRef(LinkRefStructure value) {
        this.linkRef = value;
    }

    public BigDecimal getDistanceFromStart() {
        return distanceFromStart;
    }

    public void setDistanceFromStart(BigDecimal value) {
        this.distanceFromStart = value;
    }

    public JAXBElement<? extends PointRefStructure> getPointRef() {
        return pointRef;
    }

    public void setPointRef(JAXBElement<? extends PointRefStructure> value) {
        this.pointRef = value;
    }

    public JAXBElement<? extends Point> getPoint() {
        return point;
    }

    public void setPoint(JAXBElement<? extends Point> value) {
        this.point = value;
    }

    public BigInteger getOrder() {
        return order;
    }

    public void setOrder(BigInteger value) {
        this.order = value;
    }

}
