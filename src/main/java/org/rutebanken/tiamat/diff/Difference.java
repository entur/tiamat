package org.rutebanken.tiamat.diff;

import org.geotools.data.Diff;

public class Difference {

    public DiffType diffType;
    public String property;
    public Object oldValue;
    public Object newValue;



    public Difference(String property, Object oldValue, Object newValue) {
        this.diffType = DiffType.VALUE_CHANGE;
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public Difference(DiffType diffType, String property, Object oldValue, Object newValue) {
        this.diffType = diffType;
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }


    public String toString() {

        if(DiffType.COLLECTION_ADD.equals(diffType)) {
            return property + ": added " + newValue;
        }

        if(DiffType.COLLECTION_REMOVE.equals(diffType)) {
            return property + ": removed " + oldValue;
        }


        return property + ": " + oldValue + " => " + newValue;
    }

}
