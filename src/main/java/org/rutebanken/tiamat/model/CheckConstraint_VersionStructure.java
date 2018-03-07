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
import javax.xml.bind.JAXBElement;


@MappedSuperclass
public class CheckConstraint_VersionStructure
        extends Assignment_VersionStructure {

    @Transient
    protected PlaceRef placeRef;

    @Transient
    protected CheckDirectionEnumeration checkDirection;

    @Transient
    protected CheckProcessTypeEnumeration checkProcess;

    @Transient
    protected CheckServiceEnumeration checkService;

    @Transient
    protected CongestionEnumeration congestion;

    @Transient
    protected ClassOfUseRef classOfUseRef;

    @Transient
    protected TypeOfEquipmentRefStructure typeOfEquipmentRef;

    @Transient
    protected JAXBElement<? extends EquipmentRefStructure> equipmentRef;

    @Transient
    protected Delays delays;


    public PlaceRef getPlaceRef() {
        return placeRef;
    }

    public void setPlaceRef(PlaceRef value) {
        this.placeRef = value;
    }

    public CheckDirectionEnumeration getCheckDirection() {
        return checkDirection;
    }

    public void setCheckDirection(CheckDirectionEnumeration value) {
        this.checkDirection = value;
    }

    public CheckProcessTypeEnumeration getCheckProcess() {
        return checkProcess;
    }

    public void setCheckProcess(CheckProcessTypeEnumeration value) {
        this.checkProcess = value;
    }

    public CheckServiceEnumeration getCheckService() {
        return checkService;
    }

    public void setCheckService(CheckServiceEnumeration value) {
        this.checkService = value;
    }

    public CongestionEnumeration getCongestion() {
        return congestion;
    }

    public void setCongestion(CongestionEnumeration value) {
        this.congestion = value;
    }

    public ClassOfUseRef getClassOfUseRef() {
        return classOfUseRef;
    }

    public void setClassOfUseRef(ClassOfUseRef value) {
    }

    public TypeOfEquipmentRefStructure getTypeOfEquipmentRef() {
        return typeOfEquipmentRef;
    }

    public void setTypeOfEquipmentRef(TypeOfEquipmentRefStructure value) {
        this.typeOfEquipmentRef = value;
    }

    public JAXBElement<? extends EquipmentRefStructure> getEquipmentRef() {
        return equipmentRef;
    }

    public void setEquipmentRef(JAXBElement<? extends EquipmentRefStructure> value) {
        this.equipmentRef = value;
    }

    public Delays getDelays() {
        return delays;
    }

    public void setDelays(Delays value) {
        this.delays = value;
    }

}
