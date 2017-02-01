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
