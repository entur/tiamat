package org.rutebanken.tiamat.model;

import javax.persistence.Embeddable;

@Embeddable
public class SiteRefStructure
        extends SiteElementRefStructure {

    public SiteRefStructure() { super();}

    public SiteRefStructure(String ref, String version) {
        super(ref, version);
    }

    public SiteRefStructure(String ref) {
        super(ref);
    }

}
