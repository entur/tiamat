package org.rutebanken.tiamat.model;

import javax.xml.bind.JAXBElement;
import java.util.ArrayList;
import java.util.List;


public class TransfersInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<JAXBElement<? extends Transfer_VersionStructure>> transfer;

    public List<JAXBElement<? extends Transfer_VersionStructure>> getTransfer() {
        if (transfer == null) {
            transfer = new ArrayList<JAXBElement<? extends Transfer_VersionStructure>>();
        }
        return this.transfer;
    }

}
