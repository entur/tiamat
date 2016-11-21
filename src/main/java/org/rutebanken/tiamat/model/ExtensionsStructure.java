package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class ExtensionsStructure {

    protected List<Object> any;

    public List<Object> getAny() {
        if (any == null) {
            any = new ArrayList<Object>();
        }
        return this.any;
    }

}
