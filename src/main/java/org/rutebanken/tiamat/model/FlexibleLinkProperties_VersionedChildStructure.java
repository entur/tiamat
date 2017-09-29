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


public class FlexibleLinkProperties_VersionedChildStructure
        extends VersionedChildStructure {

    protected JAXBElement<? extends LinkRefStructure> linkRef;
    protected Boolean mayBeSkipped;
    protected Boolean onMainRoute;
    protected Boolean unscheduledPath;
    protected FlexibleLinkTypeEnumeration flexibleLinkType;

    public JAXBElement<? extends LinkRefStructure> getLinkRef() {
        return linkRef;
    }

    public void setLinkRef(JAXBElement<? extends LinkRefStructure> value) {
        this.linkRef = value;
    }

    public Boolean isMayBeSkipped() {
        return mayBeSkipped;
    }

    public void setMayBeSkipped(Boolean value) {
        this.mayBeSkipped = value;
    }

    public Boolean isOnMainRoute() {
        return onMainRoute;
    }

    public void setOnMainRoute(Boolean value) {
        this.onMainRoute = value;
    }

    public Boolean isUnscheduledPath() {
        return unscheduledPath;
    }

    public void setUnscheduledPath(Boolean value) {
        this.unscheduledPath = value;
    }

    public FlexibleLinkTypeEnumeration getFlexibleLinkType() {
        return flexibleLinkType;
    }

    public void setFlexibleLinkType(FlexibleLinkTypeEnumeration value) {
        this.flexibleLinkType = value;
    }

}
