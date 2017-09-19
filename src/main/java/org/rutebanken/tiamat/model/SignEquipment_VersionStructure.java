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

import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import java.math.BigDecimal;

@MappedSuperclass
public abstract class SignEquipment_VersionStructure
        extends InstalledEquipment_VersionStructure {

    protected BigDecimal height;
    protected BigDecimal width;
    protected BigDecimal heightFromFloor;

    @Transient
    protected MultilingualStringEntity placement;
    protected String brandGraphic;
    protected String signGraphic;
    protected Boolean machineReadable;

    public BigDecimal getHeight() {
        return height;
    }

    public void setHeight(BigDecimal value) {
        this.height = value;
    }

    public BigDecimal getWidth() {
        return width;
    }

    public void setWidth(BigDecimal value) {
        this.width = value;
    }

    public BigDecimal getHeightFromFloor() {
        return heightFromFloor;
    }

    public void setHeightFromFloor(BigDecimal value) {
        this.heightFromFloor = value;
    }

    public MultilingualStringEntity getPlacement() {
        return placement;
    }

    public void setPlacement(MultilingualStringEntity value) {
        this.placement = value;
    }

    public String getBrandGraphic() {
        return brandGraphic;
    }

    public void setBrandGraphic(String value) {
        this.brandGraphic = value;
    }

    public String getSignGraphic() {
        return signGraphic;
    }

    public void setSignGraphic(String value) {
        this.signGraphic = value;
    }

    public Boolean isMachineReadable() {
        return machineReadable;
    }

    public void setMachineReadable(Boolean value) {
        this.machineReadable = value;
    }

}
