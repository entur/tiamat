package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class TransferRefs_RelStructure
        extends OneToManyRelationshipStructure {

    protected List<JAXBElement<? extends TransferRefStructure>> transferRef;

    public List<JAXBElement<? extends TransferRefStructure>> getTransferRef() {
        if (transferRef == null) {
            transferRef = new ArrayList<JAXBElement<? extends TransferRefStructure>>();
        }
        return this.transferRef;
    }

}
