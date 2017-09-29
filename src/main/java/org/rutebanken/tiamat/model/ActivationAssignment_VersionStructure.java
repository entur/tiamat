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

public class ActivationAssignment_VersionStructure
        extends Assignment_VersionStructure {

    protected ActivatedEquipmentRefStructure equipmentRef;
    protected ActivationLinkRefStructure linkRef;
    protected ActivationPointRefStructure pointRef;

    public ActivatedEquipmentRefStructure getEquipmentRef() {
        return equipmentRef;
    }

    public void setEquipmentRef(ActivatedEquipmentRefStructure value) {
        this.equipmentRef = value;
    }

    public ActivationLinkRefStructure getLinkRef() {
        return linkRef;
    }

    public void setLinkRef(ActivationLinkRefStructure value) {
        this.linkRef = value;
    }

    public ActivationPointRefStructure getPointRef() {
        return pointRef;
    }

    public void setPointRef(ActivationPointRefStructure value) {
        this.pointRef = value;
    }

}
