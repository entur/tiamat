package org.rutebanken.tiamat.model;

import java.util.ArrayList;
import java.util.List;


public class InfoLinks {

    protected List<InfoLinkStructure> infoLink;

    public List<InfoLinkStructure> getInfoLink() {
        if (infoLink == null) {
            infoLink = new ArrayList<InfoLinkStructure>();
        }
        return this.infoLink;
    }

}
