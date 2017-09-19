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

public class EscalatorEquipment_VersionStructure
        extends StairEquipment_VersionStructure {

    protected Boolean tactileActuators;
    protected Boolean energySaving;
    protected Boolean dogsMustBeCarried;

    public Boolean isTactileActuators() {
        return tactileActuators;
    }

    public void setTactileActuators(Boolean value) {
        this.tactileActuators = value;
    }

    public Boolean isEnergySaving() {
        return energySaving;
    }

    public void setEnergySaving(Boolean value) {
        this.energySaving = value;
    }

    public Boolean isDogsMustBeCarried() {
        return dogsMustBeCarried;
    }

    public void setDogsMustBeCarried(Boolean value) {
        this.dogsMustBeCarried = value;
    }

}
