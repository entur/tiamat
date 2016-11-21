

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


public class CodespaceAssignment_VersionedChildStructure
    extends VersionedChildStructure
{

    protected List<Object> rest;

    public List<Object> getRest() {
        if (rest == null) {
            rest = new ArrayList<Object>();
        }
        return this.rest;
    }

}
