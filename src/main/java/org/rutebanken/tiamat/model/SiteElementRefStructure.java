package org.rutebanken.tiamat.model;

import javax.persistence.MappedSuperclass;


@MappedSuperclass
public class SiteElementRefStructure extends AddressablePlaceRefStructure {

    public SiteElementRefStructure() { super();}

    public SiteElementRefStructure(String ref, String version) {
        super(ref, version);
    }

    public SiteElementRefStructure(String ref) {
        super(ref);
    }

}
