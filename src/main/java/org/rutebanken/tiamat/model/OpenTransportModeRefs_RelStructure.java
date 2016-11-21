

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class OpenTransportModeRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<OpenTransportModeRefStructure> openTransportModeRef;

    public List<OpenTransportModeRefStructure> getOpenTransportModeRef() {
        if (openTransportModeRef == null) {
            openTransportModeRef = new ArrayList<OpenTransportModeRefStructure>();
        }
        return this.openTransportModeRef;
    }

}
