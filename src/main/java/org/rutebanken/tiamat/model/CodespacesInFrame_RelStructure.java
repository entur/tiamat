package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class CodespacesInFrame_RelStructure
        extends ContainmentAggregationStructure {

    protected List<Codespace> codespace;

    public List<Codespace> getCodespace() {
        if (codespace == null) {
            codespace = new ArrayList<Codespace>();
        }
        return this.codespace;
    }

}
