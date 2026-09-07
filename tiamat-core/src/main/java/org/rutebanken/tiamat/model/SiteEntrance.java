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

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;

import java.math.BigDecimal;


@MappedSuperclass
public class SiteEntrance extends SiteComponent_VersionStructure {

    protected String publicCode;

    // Was MultilingualStringEntity, an @Entity which cannot be @Embedded. Retyped to the
    // @Embeddable EmbeddableMultilingualString, matching ParkingComponent_VersionStructure
    // and StopPlaceSpace_VersionStructure, which already type `label` this way.
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "value", column = @Column(name = "label_value")),
            @AttributeOverride(name = "lang", column = @Column(name = "label_lang", length = 5))
    })
    protected EmbeddableMultilingualString label;

    @Enumerated(EnumType.STRING)
    protected EntranceEnumeration entranceType;
    protected Boolean isExternal;
    protected Boolean isEntry;
    protected Boolean isExit;
    @Column(precision = 10, scale = 2)
    protected BigDecimal width;
    @Column(precision = 10, scale = 2)
    protected BigDecimal height;
    protected Boolean droppedKerbOutside;
    protected Boolean dropOffPointClose;

    public String getPublicCode() {
        return publicCode;
    }

    public void setPublicCode(String value) {
        this.publicCode = value;
    }

    public EmbeddableMultilingualString getLabel() {
        return label;
    }

    public void setLabel(EmbeddableMultilingualString value) {
        this.label = value;
    }

    public EntranceEnumeration getEntranceType() {
        return entranceType;
    }

    public void setEntranceType(EntranceEnumeration value) {
        this.entranceType = value;
    }

    public Boolean isIsExternal() {
        return isExternal;
    }

    public void setIsExternal(Boolean value) {
        this.isExternal = value;
    }

    public Boolean isIsEntry() {
        return isEntry;
    }

    public void setIsEntry(Boolean value) {
        this.isEntry = value;
    }

    public Boolean isIsExit() {
        return isExit;
    }

    public void setIsExit(Boolean value) {
        this.isExit = value;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal value) {
        this.width = value;
    }

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal value) {
        this.height = value;
    }

    public Boolean isDroppedKerbOutside() {
        return droppedKerbOutside;
    }

    public void setDroppedKerbOutside(Boolean value) {
        this.droppedKerbOutside = value;
    }

    public Boolean isDropOffPointClose() {
        return dropOffPointClose;
    }

    public void setDropOffPointClose(Boolean value) {
        this.dropOffPointClose = value;
    }

}
