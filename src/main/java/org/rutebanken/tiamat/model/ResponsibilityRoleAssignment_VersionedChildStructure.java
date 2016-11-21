

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;


public class ResponsibilityRoleAssignment_VersionedChildStructure
    extends VersionedChildStructure
{

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
            dataRoleType = new ArrayList<DataRoleTypeEnumeration>();
        }
        return this.dataRoleType;
    }

    public List<StakeholderRoleTypeEnumeration> getStakeholderRoleType() {
        if (stakeholderRoleType == null) {
            stakeholderRoleType = new ArrayList<StakeholderRoleTypeEnumeration>();
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
