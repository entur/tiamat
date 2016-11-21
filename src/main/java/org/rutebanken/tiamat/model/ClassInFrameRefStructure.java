package org.rutebanken.tiamat.model;

public class ClassInFrameRefStructure
        extends ClassRefStructure {

    protected ClassRefTypeEnumeration classRefType;

    public ClassRefTypeEnumeration getClassRefType() {
        if (classRefType == null) {
            return ClassRefTypeEnumeration.MEMBERS;
        } else {
            return classRefType;
        }
    }

    public void setClassRefType(ClassRefTypeEnumeration value) {
    }

}
