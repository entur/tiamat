

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlType;


public class TransfersInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<JAXBElement<? extends Transfer_VersionStructure>> transfer;

    public List<JAXBElement<? extends Transfer_VersionStructure>> getTransfer() {
        if (transfer == null) {
            transfer = new ArrayList<JAXBElement<? extends Transfer_VersionStructure>>();
        }
        return this.transfer;
    }

}
