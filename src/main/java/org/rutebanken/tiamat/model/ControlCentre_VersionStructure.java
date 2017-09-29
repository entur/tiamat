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

import java.math.BigInteger;


public class ControlCentre_VersionStructure
        extends OrganisationPart_VersionStructure {

    protected BigInteger number;
    protected MultilingualStringEntity controlCentreCode;
    protected DepartmentRefStructure departmentRef;

    public BigInteger getNumber() {
        return number;
    }

    public void setNumber(BigInteger value) {
        this.number = value;
    }

    public MultilingualStringEntity getControlCentreCode() {
        return controlCentreCode;
    }

    public void setControlCentreCode(MultilingualStringEntity value) {
        this.controlCentreCode = value;
    }

    public DepartmentRefStructure getDepartmentRef() {
        return departmentRef;
    }

    public void setDepartmentRef(DepartmentRefStructure value) {
        this.departmentRef = value;
    }

}
