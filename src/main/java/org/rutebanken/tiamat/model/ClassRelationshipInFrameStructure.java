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

public class ClassRelationshipInFrameStructure {

    protected RelationshipRefStructure relationshipRef;
    protected MandatoryEnumeration mandatory;
    protected ContainmentEnumeration containment;
    protected ModificationSetEnumeration modificationSet;
    protected String name;

    public RelationshipRefStructure getRelationshipRef() {
        return relationshipRef;
    }

    public void setRelationshipRef(RelationshipRefStructure value) {
        this.relationshipRef = value;
    }

    public MandatoryEnumeration getMandatory() {
        return mandatory;
    }

    public void setMandatory(MandatoryEnumeration value) {
        this.mandatory = value;
    }

    public ContainmentEnumeration getContainment() {
        return containment;
    }

    public void setContainment(ContainmentEnumeration value) {
        this.containment = value;
    }

    public ModificationSetEnumeration getModificationSet() {
        return modificationSet;
    }

    public void setModificationSet(ModificationSetEnumeration value) {
        this.modificationSet = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String value) {
        this.name = value;
    }

}
