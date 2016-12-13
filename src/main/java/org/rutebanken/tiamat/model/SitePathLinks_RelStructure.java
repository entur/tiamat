package org.rutebanken.tiamat.model;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.SequenceGenerator;
import java.util.ArrayList;
import java.util.List;


@Entity
public class SitePathLinks_RelStructure
        extends ContainmentAggregationStructure {

    @ElementCollection(targetClass = PathLinkRefStructure.class)
    protected List<PathLinkRefStructure> pathLinkRefOrSitePathLink;

    public List<PathLinkRefStructure> getPathLinkRefOrSitePathLink() {
        if (pathLinkRefOrSitePathLink == null) {
            pathLinkRefOrSitePathLink = new ArrayList<PathLinkRefStructure>();
        }
        return this.pathLinkRefOrSitePathLink;
    }

}
