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

import java.math.BigDecimal;


public class PassengerSafetyEquipment_VersionStructure
        extends PassengerEquipment_VersionStructure {

    protected Boolean cctv;
    protected Boolean mobilePhoneCoverage;
    protected Boolean panicButton;
    protected Boolean sosPhones;
    protected BigDecimal heightOfSosPanel;
    protected LightingEnumeration lighting;
    protected Boolean acousticAnnouncements;

    public Boolean isCctv() {
        return cctv;
    }

    public void setCctv(Boolean value) {
        this.cctv = value;
    }

    public Boolean isMobilePhoneCoverage() {
        return mobilePhoneCoverage;
    }

    public void setMobilePhoneCoverage(Boolean value) {
        this.mobilePhoneCoverage = value;
    }

    public Boolean isPanicButton() {
        return panicButton;
    }

    public void setPanicButton(Boolean value) {
        this.panicButton = value;
    }

    public Boolean isSosPhones() {
        return sosPhones;
    }

    public void setSosPhones(Boolean value) {
        this.sosPhones = value;
    }

    public BigDecimal getHeightOfSosPanel() {
        return heightOfSosPanel;
    }

    public void setHeightOfSosPanel(BigDecimal value) {
        this.heightOfSosPanel = value;
    }

    public LightingEnumeration getLighting() {
        return lighting;
    }

    public void setLighting(LightingEnumeration value) {
        this.lighting = value;
    }

    public Boolean isAcousticAnnouncements() {
        return acousticAnnouncements;
    }

    public void setAcousticAnnouncements(Boolean value) {
        this.acousticAnnouncements = value;
    }

}
