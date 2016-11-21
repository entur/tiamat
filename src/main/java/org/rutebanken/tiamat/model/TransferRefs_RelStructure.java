

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class TransferRefs_RelStructure
    extends OneToManyRelationshipStructure
{

    protected List<JAXBElement<? extends TransferRefStructure>> transferRef;

    public List<JAXBElement<? extends TransferRefStructure>> getTransferRef() {
        if (transferRef == null) {
            transferRef = new ArrayList<JAXBElement<? extends TransferRefStructure>>();
        }
        return this.transferRef;
    }

}
