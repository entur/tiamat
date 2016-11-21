

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class GroupConstraintMember_VersionedChildStructure
    extends VersionedChildStructure
{

    protected PurposeOfGroupingRefStructure purposeOfGroupingRef;
    protected ClassRefStructure memberClassRef;
    protected TypeOfValueRefStructure memberTypeOfValueRef;

    public PurposeOfGroupingRefStructure getPurposeOfGroupingRef() {
        return purposeOfGroupingRef;
    }

    public void setPurposeOfGroupingRef(PurposeOfGroupingRefStructure value) {
        this.purposeOfGroupingRef = value;
    }

    public ClassRefStructure getMemberClassRef() {
        return memberClassRef;
    }

    public void setMemberClassRef(ClassRefStructure value) {
        this.memberClassRef = value;
    }

    public TypeOfValueRefStructure getMemberTypeOfValueRef() {
        return memberTypeOfValueRef;
    }

    public void setMemberTypeOfValueRef(TypeOfValueRefStructure value) {
        this.memberTypeOfValueRef = value;
    }

}
