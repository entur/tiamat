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


public class SchematicMapMember_VersionedChildStructure
        extends VersionedChildStructure {

    protected MultilingualStringEntity name;
    protected JAXBElement<? extends VersionOfObjectRefStructure> versionOfObjectRef;
    protected Boolean hide;
    protected Boolean displayAsIcon;
    protected InfoLinkStructure infoLink;
    protected Float x;
    protected Float y;
    protected JAXBElement<? extends Projection_VersionStructure> projection;

    public MultilingualStringEntity getName() {
        return name;
    }

    public void setName(MultilingualStringEntity value) {
        this.name = value;
    }

    public JAXBElement<? extends VersionOfObjectRefStructure> getVersionOfObjectRef() {
        return versionOfObjectRef;
    }

    public void setVersionOfObjectRef(JAXBElement<? extends VersionOfObjectRefStructure> value) {
        this.versionOfObjectRef = value;
    }

    public Boolean isHide() {
        return hide;
    }

    public void setHide(Boolean value) {
        this.hide = value;
    }

    public Boolean isDisplayAsIcon() {
        return displayAsIcon;
    }

    public void setDisplayAsIcon(Boolean value) {
        this.displayAsIcon = value;
    }

    public InfoLinkStructure getInfoLink() {
        return infoLink;
    }

    public void setInfoLink(InfoLinkStructure value) {
        this.infoLink = value;
    }

    public Float getX() {
        return x;
    }

    public void setX(Float value) {
        this.x = value;
    }

    public Float getY() {
        return y;
    }

    public void setY(Float value) {
        this.y = value;
    }

    public JAXBElement<? extends Projection_VersionStructure> getProjection() {
        return projection;
    }

    public void setProjection(JAXBElement<? extends Projection_VersionStructure> value) {
        this.projection = value;
    }

}
