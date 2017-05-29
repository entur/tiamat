package org.rutebanken.tiamat.diff;

public class Difference {

    public Difference(String property, Object oldValue, Object newValue) {
        this.property = property;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String property;
    public Object oldValue;
    public Object newValue;


    public String toString() {
        return property + ": " + oldValue + " => " + newValue;
    }

}
