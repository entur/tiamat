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

public class GroupConstraintMember_VersionedChildStructure
        extends VersionedChildStructure {

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
