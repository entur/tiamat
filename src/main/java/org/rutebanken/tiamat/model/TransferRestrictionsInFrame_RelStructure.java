

package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


public class TransferRestrictionsInFrame_RelStructure
    extends ContainmentAggregationStructure
{

    protected List<TransferRestriction> transferRestriction;

    public List<TransferRestriction> getTransferRestriction() {
        if (transferRestriction == null) {
            transferRestriction = new ArrayList<TransferRestriction>();
        }
        return this.transferRestriction;
    }

}
