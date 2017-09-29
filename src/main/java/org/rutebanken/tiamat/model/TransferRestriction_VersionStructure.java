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

public class TransferRestriction_VersionStructure
        extends Assignment_VersionStructure {

    protected TypeOfTransferRefStructure typeOfTransferRef;
    protected Boolean bothWays;
    protected TransferConstraintTypeEnumeration restrictionType;

    public TypeOfTransferRefStructure getTypeOfTransferRef() {
        return typeOfTransferRef;
    }

    public void setTypeOfTransferRef(TypeOfTransferRefStructure value) {
        this.typeOfTransferRef = value;
    }

    public Boolean isBothWays() {
        return bothWays;
    }

    public void setBothWays(Boolean value) {
        this.bothWays = value;
    }

    public TransferConstraintTypeEnumeration getRestrictionType() {
        return restrictionType;
    }

    public void setRestrictionType(TransferConstraintTypeEnumeration value) {
        this.restrictionType = value;
    }

}
