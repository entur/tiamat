

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class Traces_RelStructure {

    protected List<Trace> trace;

    public List<Trace> getTrace() {
        if (trace == null) {
            trace = new ArrayList<Trace>();
        }
        return this.trace;
    }

}
