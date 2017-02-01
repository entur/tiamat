package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class TransferRestrictionsInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<TransferRestriction> transferRestriction;

    public List<TransferRestriction> getTransferRestriction() {
        if (transferRestriction == null) {
            transferRestriction = new ArrayList<TransferRestriction>();
        }
        return this.transferRestriction;
    }

}
