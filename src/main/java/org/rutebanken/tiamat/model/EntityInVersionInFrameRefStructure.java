package org.rutebanken.tiamat.model;

public class EntityInVersionInFrameRefStructure
        extends VersionOfObjectRefStructure {

    protected ModificationEnumeration modification;
    protected String id;

    public ModificationEnumeration getModification() {
        if (modification == null) {
            return ModificationEnumeration.NEW;
        } else {
            return modification;
        }
    }

    public void setModification(ModificationEnumeration value) {
        this.modification = value;
    }

    public String getId() {
        return id;
    }

    public void setId(String value) {
        this.id = value;
    }

}
