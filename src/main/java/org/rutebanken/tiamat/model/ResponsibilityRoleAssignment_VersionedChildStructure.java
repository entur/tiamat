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

import java.util.ArrayList;
import java.util.List;


public class ResponsibilityRoleAssignment_VersionedChildStructure
        extends VersionedChildStructure {

    protected ResponsibilitySetRefStructure responsibilitySetRef;
    protected MultilingualStringEntity description;
    protected List<DataRoleTypeEnumeration> dataRoleType;
    protected List<StakeholderRoleTypeEnumeration> stakeholderRoleType;
    protected TypeOfResponsibilityRoleRefStructure typeOfResponsibilityRoleRef;
    protected OrganisationRefStructure responsibleOrganisationRef;
    protected OrganisationPartRefStructure responsiblePartRef;
    protected VersionOfObjectRefStructure responsibleAreaRef;

    public ResponsibilitySetRefStructure getResponsibilitySetRef() {
        return responsibilitySetRef;
    }

    public void setResponsibilitySetRef(ResponsibilitySetRefStructure value) {
        this.responsibilitySetRef = value;
    }

    public MultilingualStringEntity getDescription() {
        return description;
    }

    public void setDescription(MultilingualStringEntity value) {
        this.description = value;
    }

    public List<DataRoleTypeEnumeration> getDataRoleType() {
        if (dataRoleType == null) {
            dataRoleType = new ArrayList<>();
        }
        return this.dataRoleType;
    }

    public List<StakeholderRoleTypeEnumeration> getStakeholderRoleType() {
        if (stakeholderRoleType == null) {
            stakeholderRoleType = new ArrayList<>();
        }
        return this.stakeholderRoleType;
    }

    public TypeOfResponsibilityRoleRefStructure getTypeOfResponsibilityRoleRef() {
        return typeOfResponsibilityRoleRef;
    }

    public void setTypeOfResponsibilityRoleRef(TypeOfResponsibilityRoleRefStructure value) {
        this.typeOfResponsibilityRoleRef = value;
    }

    public OrganisationRefStructure getResponsibleOrganisationRef() {
        return responsibleOrganisationRef;
    }

    public void setResponsibleOrganisationRef(OrganisationRefStructure value) {
        this.responsibleOrganisationRef = value;
    }

    public OrganisationPartRefStructure getResponsiblePartRef() {
        return responsiblePartRef;
    }

    public void setResponsiblePartRef(OrganisationPartRefStructure value) {
        this.responsiblePartRef = value;
    }

    public VersionOfObjectRefStructure getResponsibleAreaRef() {
        return responsibleAreaRef;
    }

    public void setResponsibleAreaRef(VersionOfObjectRefStructure value) {
        this.responsibleAreaRef = value;
    }

}
