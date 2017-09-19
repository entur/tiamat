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

public class TypeOfFrame_ValueStructure
        extends TypeOfEntity_VersionStructure {

    protected TypeOfValidityRefStructure typeOfValidityRef;
    protected ClassRefStructure frameClassRef;
    protected ClassesInRepository_RelStructure classes;
    protected TypesOfFrame_RelStructure includes;
    protected String locatingSystemRef;
    protected ModificationSetEnumeration modificationSet;

    public TypeOfValidityRefStructure getTypeOfValidityRef() {
        return typeOfValidityRef;
    }

    public void setTypeOfValidityRef(TypeOfValidityRefStructure value) {
        this.typeOfValidityRef = value;
    }

    public ClassRefStructure getFrameClassRef() {
        return frameClassRef;
    }

    public void setFrameClassRef(ClassRefStructure value) {
        this.frameClassRef = value;
    }

    public ClassesInRepository_RelStructure getClasses() {
        return classes;
    }

    public void setClasses(ClassesInRepository_RelStructure value) {
    }

    public TypesOfFrame_RelStructure getIncludes() {
        return includes;
    }

    public void setIncludes(TypesOfFrame_RelStructure value) {
        this.includes = value;
    }

    public String getLocatingSystemRef() {
        return locatingSystemRef;
    }

    public void setLocatingSystemRef(String value) {
        this.locatingSystemRef = value;
    }

    public ModificationSetEnumeration getModificationSet() {
        return modificationSet;
    }

    public void setModificationSet(ModificationSetEnumeration value) {
        this.modificationSet = value;
    }

}
