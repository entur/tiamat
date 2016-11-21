

package org.rutebanken.tiamat.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;


public class ClassInFrameRefStructure
    extends ClassRefStructure
{

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
