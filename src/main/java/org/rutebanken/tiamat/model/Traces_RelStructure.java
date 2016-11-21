package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class Traces_RelStructure {

    protected List<Trace> trace;

    public List<Trace> getTrace() {
        if (trace == null) {
            trace = new ArrayList<Trace>();
        }
        return this.trace;
    }

}
