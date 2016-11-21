package org.rutebanken.tiamat.model;

public class OneToManyRelationshipStructure
        extends RelationshipStructure {

    protected ModificationSetEnumeration modificationSet;

    public ModificationSetEnumeration getModificationSet() {
        if (modificationSet == null) {
            return ModificationSetEnumeration.ALL;
        } else {
            return modificationSet;
        }
    }

    public void setModificationSet(ModificationSetEnumeration value) {
        this.modificationSet = value;
    }

}
