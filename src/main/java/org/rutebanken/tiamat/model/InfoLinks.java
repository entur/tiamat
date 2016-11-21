

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class InfoLinks {

    protected List<InfoLinkStructure> infoLink;

    public List<InfoLinkStructure> getInfoLink() {
        if (infoLink == null) {
            infoLink = new ArrayList<InfoLinkStructure>();
        }
        return this.infoLink;
    }

}
